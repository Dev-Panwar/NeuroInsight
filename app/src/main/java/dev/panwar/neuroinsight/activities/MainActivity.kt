package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.api.RetrofitInstance
import dev.panwar.neuroinsight.databinding.ActivityMainBinding
import dev.panwar.neuroinsight.models.response.PersonalDetailsResponse
import dev.panwar.neuroinsight.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var authenticationToken:String
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog("Please wait")
        val sharedPreferences=getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val authToken=sharedPreferences.getString(Constants.AUTH_TOKEN,"")
        authenticationToken=authToken!!

        val call: Call<PersonalDetailsResponse> = RetrofitInstance.api.getPersonalDetails("Bearer ${authToken}")
        call.enqueue(object : Callback<PersonalDetailsResponse> {
            override fun onResponse(call: Call<PersonalDetailsResponse>, response: Response<PersonalDetailsResponse>) {
                if (response.isSuccessful) {
                    hideProgressDialogue()
                    Log.e("GetUserResponse",response.body().toString())
                    val registrationResponse = response.body()
//                    if(registrationResponse!=null && registrationResponse.size >0){
                        startActivity(Intent(this@MainActivity,SurveyHome::class.java))
                        finish()
//                    }else{
                        startActivity(Intent(this@MainActivity,PersonalDetailsActivity::class.java))
                        finish()
//                    }

                } else {
                    hideProgressDialogue()
                    if (response.code()==401){
                        Toast.makeText(this@MainActivity, "Session Expired. Please Login again", Toast.LENGTH_SHORT).show()
                        deleteToken()
                        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                        finish()
                    }

                }
            }

            override fun onFailure(call: Call<PersonalDetailsResponse>, t: Throwable) {
                hideProgressDialogue()
                // Handle failure (e.g., network issues)
                Toast.makeText(this@MainActivity, "Request failed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity,LoginActivity::class.java))
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
        binding = null
    }
}