package dev.panwar.neuroinsight.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {

    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //        for changing the Font of displayed title..i.e. APP name
        val typeFace: Typeface = Typeface.createFromAsset(assets,"Carnevalee Freakshow.ttf")
        binding?.tvAppName?.typeface=typeFace

//        to Move to Intro Activity automatically after 1.5 secs
        Handler().postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()//finishes this activity
        },2000)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}