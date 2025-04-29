package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.adapter.SurveyAdapter
import dev.panwar.neuroinsight.api.RetrofitInstance
import dev.panwar.neuroinsight.databinding.ActivityFhqsurveyBinding
import dev.panwar.neuroinsight.models.request.QuestionResponses
import dev.panwar.neuroinsight.models.response.GetGADQuestionsResponse
import dev.panwar.neuroinsight.models.response.GetGADQuestionsResponseItem
import dev.panwar.neuroinsight.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FHQSurvey : BaseActivity() {

    private var binding: ActivityFhqsurveyBinding?=null
    private val responses = mutableMapOf<String,String>()
    private lateinit var questionsList: List<GetGADQuestionsResponseItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFhqsurveyBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupLayout()

        binding?.tvTitle?.setOnClickListener {
            onBackPressed()
        }

        binding?.btnSubmit?.setOnClickListener {
            if (responses.size == questionsList.size){
                Log.d("response",responses.toString())
                submitResponses(responses)
            }else{
                showToast("All questions are mandatory")
            }
        }

    }

    private fun submitResponses(responses: MutableMap<String, String>) {
        val questionResponses = mutableListOf<QuestionResponses>()
        for (response in responses){
            questionResponses.add(QuestionResponses(question = response.key, response = response.value))
        }

        showProgressDialog("Please wait")
        val sharedPreferences=getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val authToken=sharedPreferences.getString(Constants.AUTH_TOKEN,"")

        val call: Call<ResponseBody> = RetrofitInstance.api.postFHQResponses("Bearer ${authToken}",questionResponses)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    hideProgressDialogue()
                    val responseBody = response.body()?.string() ?: ""  // Convert ResponseBody to String
                    Log.e("response", "Response: '$responseBody'")

                    if (responseBody.trim() == "PHQ Responses added successfully") {
                        showToast("Successfully submitted")
                        showGADResultPopup(this@FHQSurvey, responses)
                    } else {
                        showToast("Error in submitting")
                    }
                } else {
                    hideProgressDialogue()
                    if (response.code()==401){
                        Toast.makeText(this@FHQSurvey, "Session Expired. Please Login again", Toast.LENGTH_SHORT).show()
                        deleteToken()
                        startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@FHQSurvey, "Network Error", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
                        finish()
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideProgressDialogue()
                // Handle failure (e.g., network issues)
                Log.e("Error",t.message.toString())
                Toast.makeText(this@FHQSurvey, "Request failed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
            }
        })

    }

    fun showGADResultPopup(context: Context, responses: Map<String, String>) {
        val totalScore = calculateGADScore(responses)

        // Interpret the score based on clinical guidelines
        val severity = when {
            totalScore in 0..4 -> "Minimal Anxiety"
            totalScore in 5..9 -> "Mild Anxiety"
            totalScore in 10..14 -> "Moderate Anxiety"
            totalScore >= 15 -> "Severe Anxiety"
            else -> "Invalid Score"
        }

        AlertDialog.Builder(context)
            .setTitle("Your PHQ Assessment Result")
            .setMessage("Total Score: $totalScore\nSeverity Level: $severity")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                deleteToken()
                startActivity(Intent(context, LoginActivity::class.java))
                finish()
            }
            .show()
    }


    fun calculateGADScore(responses: Map<String, String>): Int {
        val scoreMap = mapOf(
            "Not at all" to 0,
            "Several days" to 1,
            "More than half of days" to 2,
            "Nearly every day" to 3
        )

        return responses.values.sumOf { scoreMap[it] ?: 0 }
    }



    private fun setupLayout() {
        showProgressDialog("Please wait...")
        val sharedPreferences=getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val authToken=sharedPreferences.getString(Constants.AUTH_TOKEN,"")

        val call: Call<GetGADQuestionsResponse> = RetrofitInstance.api.getFHQQuestions("Bearer ${authToken}")
        call.enqueue(object : Callback<GetGADQuestionsResponse> {
            override fun onResponse(call: Call<GetGADQuestionsResponse>, response: Response<GetGADQuestionsResponse>) {
                if (response.isSuccessful) {
                    hideProgressDialogue()
                    Log.e("GetUserResponse",response.body().toString())
                    val list = response.body()
                    if (list != null) {
                        questionsList=list.toList()
                    }
                    if(list!=null && list.size >0){
                        val rv=binding?.recyclerViewSurvey!!
                        rv.layoutManager= LinearLayoutManager(this@FHQSurvey)
                        val adapter= SurveyAdapter(this@FHQSurvey,list, responses)
                        rv.adapter=adapter

                    }

                } else {
                    hideProgressDialogue()
                    if (response.code()==401){
                        Toast.makeText(this@FHQSurvey, "Session Expired. Please Login again", Toast.LENGTH_SHORT).show()
                        deleteToken()
                        startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@FHQSurvey, "Network Error", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
                        finish()
                    }

                }
            }

            override fun onFailure(call: Call<GetGADQuestionsResponse>, t: Throwable) {
                hideProgressDialogue()
                // Handle failure (e.g., network issues)
                Toast.makeText(this@FHQSurvey, "Request failed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@FHQSurvey,LoginActivity::class.java))
            }
        })
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