package dev.panwar.neuroinsight.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.databinding.ActivityGadresultBinding
import dev.panwar.neuroinsight.utils.Constants

class GADResult : AppCompatActivity() {
    private var binding: ActivityGadresultBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGadresultBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val responses: HashMap<String, String>? = intent.getSerializableExtra("responsesMap") as? HashMap<String, String>
        if (responses != null) {
            showGADResultPopup(this, responses)
        }

        binding?.btnLogout?.setOnClickListener {
            deleteToken()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
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

        binding?.tvGadScore?.text= totalScore.toString()
        binding?.tvSeverityScore?.text = severity
    }

    private fun deleteToken(){
        // Clear the authentication token (e.g., remove it from SharedPreferences)
        val sharedPreferences = getSharedPreferences(Constants.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(Constants.AUTH_TOKEN)
        editor.apply()
    }

}