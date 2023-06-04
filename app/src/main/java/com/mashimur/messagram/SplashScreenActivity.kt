package com.mashimur.messagram

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mashimur.messagram.messages.LatestMessagesActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        val thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    startActivity(Intent(this@SplashScreenActivity,LatestMessagesActivity::class.java))
                }
            }
        }

        thread.start()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}