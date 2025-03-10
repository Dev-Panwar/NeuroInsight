package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.databinding.ActivitySurveyHomeBinding
import dev.panwar.neuroinsight.utils.Constants

class SurveyHome : AppCompatActivity() {

    private var binding: ActivitySurveyHomeBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySurveyHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnGADSurvey?.setOnClickListener {
            startActivity(Intent(this,GADSurvey::class.java))
        }

        binding?.btnFHQSurvey?.setOnClickListener {
            startActivity(Intent(this,FHQSurvey::class.java))
        }

        binding?.btnLogout?.setOnClickListener {
            deleteToken()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding?.btnGADSurvey?.callOnClick()


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