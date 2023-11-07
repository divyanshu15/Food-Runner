package com.divyanshu.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.divyanshu.foodrunner.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val intentToLoginActivity = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intentToLoginActivity)
            finish()
        }, 2000)
    }
}