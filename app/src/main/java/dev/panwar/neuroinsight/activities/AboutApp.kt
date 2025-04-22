package dev.panwar.neuroinsight.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.databinding.ActivityAboutAppBinding
import java.net.URLEncoder

class AboutApp : AppCompatActivity() {

    private var binding:ActivityAboutAppBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnAppSupport?.setOnClickListener {
            openWhatsAppChat("+919516702351") // Replace with the actual phone number
        }

    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }

    private fun openWhatsAppChat(phoneNumber: String) {
        try {
            // Format the phone number properly (remove any spaces, dashes, etc.)
            val formattedNumber = phoneNumber.replace(Regex("[^0-9+]"), "")

            // Create the WhatsApp URI with the message included
            val message = URLEncoder.encode("Hello, I have a query regarding NeuroInsight: ", "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=$message")

            // Create an intent with ACTION_VIEW
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri

            // Start the activity
            startActivity(intent)
        } catch (e: Exception) {
            // Handle the exception - WhatsApp not installed or other issues
            Toast.makeText(this, "WhatsApp not installed or couldn't be opened", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}