package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.api.RetrofitInstance
import dev.panwar.neuroinsight.databinding.ActivityPersonalDetailsBinding
import dev.panwar.neuroinsight.models.request.PersonalDetailsRequest
import dev.panwar.neuroinsight.models.response.PersonalDetailsResponse
import dev.panwar.neuroinsight.models.response.PersonalDetailsResponseItem
import dev.panwar.neuroinsight.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetailsActivity : BaseActivity() {

    private var binding: ActivityPersonalDetailsBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val genderOptions = resources.getStringArray(R.array.gender_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding?.etGender?.setAdapter(adapter)

        val maritalStatusOptions = resources.getStringArray(R.array.marital_status_options)
        val maritalStatusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, maritalStatusOptions)
        binding?.etMaritalStatus?.setAdapter(maritalStatusAdapter)

        val educationOptions = resources.getStringArray(R.array.education_options)
        val educationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, educationOptions)
        binding?.etEducation?.setAdapter(educationAdapter)

        val jobStatusOptions = resources.getStringArray(R.array.job_status_options)
        val jobStatusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jobStatusOptions)
        binding?.etJobStatusDuringLockdown?.setAdapter(jobStatusAdapter)

        val jobNatureOptions = resources.getStringArray(R.array.job_nature_options)
        val jobNatureAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jobNatureOptions)
        binding?.etJobNature?.setAdapter(jobNatureAdapter)

        val yesNoOptions = resources.getStringArray(R.array.yes_no_options)
        val yesNoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, yesNoOptions)
        binding?.etCovidInfected?.setAdapter(yesNoAdapter)
        binding?.etKnowCovidInfected?.setAdapter(yesNoAdapter)
        binding?.etDepressionHistory?.setAdapter(yesNoAdapter)
        binding?.etLongIllness?.setAdapter(yesNoAdapter)
        binding?.etPracticeYoga?.setAdapter(yesNoAdapter)

        val sharedPreferences=getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
        val userEmail=sharedPreferences.getString("email","")
        binding?.tvEmail?.text="User: ${userEmail}"


        binding?.btnSubmitDetails?.setOnClickListener {
            submitPersonalDetails()
        }

        binding?.btnSkip?.setOnClickListener {
            startActivity(Intent(this,SurveyHome::class.java))
            finish()
        }


    }

    private fun submitPersonalDetails() {
        val gender = binding?.etGender?.text.toString().trim()
        val age = binding?.etAge?.text.toString().trim()
        val maritalStatus = binding?.etMaritalStatus?.text.toString().trim()
        val duringLockdownStayingAt = binding?.etDuringLockdownStayingAt?.text.toString().trim()
        val state = binding?.etState?.text.toString().trim()
        val education = binding?.etEducation?.text.toString().trim()
        val job = binding?.etJobPosition?.text.toString().trim()
        val jobNature = binding?.etJobNature?.text.toString().trim()
        val jobStatusDuringLockdown = binding?.etJobStatusDuringLockdown?.text.toString().trim()
        val monthlyIncome=binding?.etMonthlyIncome?.text.toString().trim()
        val knowCovidInfected = binding?.etKnowCovidInfected?.text.toString().trim()
        val covidInfected = binding?.etCovidInfected?.text.toString().trim()
        val depressionHistory = binding?.etDepressionHistory?.text.toString().trim()
        val longIllness = binding?.etLongIllness?.text.toString().trim()
        val practiceYoga = binding?.etPracticeYoga?.text.toString().trim()

        if (gender.isEmpty() || age.isEmpty() || maritalStatus.isEmpty() || duringLockdownStayingAt.isEmpty() || state.isEmpty() || education.isEmpty() || job.isEmpty() || jobNature.isEmpty() || jobStatusDuringLockdown.isEmpty() || monthlyIncome.isEmpty() || knowCovidInfected.isEmpty() || covidInfected.isEmpty() || depressionHistory.isEmpty() || longIllness.isEmpty() || practiceYoga.isEmpty()) {
            showToast("Please fill all the details")
        }else{
            showProgressDialog("Please wait")
            val sharedPreferences=getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
            val authToken=sharedPreferences.getString(Constants.AUTH_TOKEN,"")
            val post = PersonalDetailsRequest(gender = gender, age = age, maritalStatus = maritalStatus, duringLockdownStayingAt = duringLockdownStayingAt, state = state, education = education, job = job, jobNature = jobNature, jobStatusDuringLockdown = jobStatusDuringLockdown, monthlyIncome = monthlyIncome.toInt(), knownCovidInfected = knowCovidInfected, covidInfected = covidInfected, depressionHistory = depressionHistory, longIllness = longIllness, practiceYoga = practiceYoga)
            val call: Call<ResponseBody> = RetrofitInstance.api.postPersonalDetails("Bearer ${authToken}",post)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        hideProgressDialogue()
                        val responseBody = response.body()?.string() ?: ""  // Convert ResponseBody to String
                        Log.e("response", "Response: '$responseBody'")

                        if (responseBody.trim() == "Personal Details Response added successfully") {
                            showToast("Successfully submitted details")
                            startActivity(Intent(this@PersonalDetailsActivity, SurveyHome::class.java))
                            finish()
                        } else {
                            showToast("Error in submitting details")
                        }
                    } else {
                        hideProgressDialogue()
                        if (response.code()==401){
                            Toast.makeText(this@PersonalDetailsActivity, "Session Expired. Please Login again", Toast.LENGTH_SHORT).show()
                            deleteToken()
                            startActivity(Intent(this@PersonalDetailsActivity,LoginActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this@PersonalDetailsActivity, "Network Error", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@PersonalDetailsActivity,LoginActivity::class.java))
                            finish()
                        }

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    hideProgressDialogue()
                    // Handle failure (e.g., network issues)
                    Log.e("Error",t.message.toString())
                    Toast.makeText(this@PersonalDetailsActivity, "Request failed", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@PersonalDetailsActivity,LoginActivity::class.java))
                }
            })
        }
    }

    private fun deleteToken(){
        // Clear the authentication token (e.g., remove it from SharedPreferences)
        val sharedPreferences = getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(Constants.AUTH_TOKEN)
        editor.apply()

    }


    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}