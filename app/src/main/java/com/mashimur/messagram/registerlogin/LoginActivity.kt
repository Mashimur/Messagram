package com.mashimur.messagram.registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mashimur.messagram.R
import com.mashimur.messagram.messages.ChatLogActivity
import com.mashimur.messagram.messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var button: Button
    private lateinit var backregisterTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        emailEditText = findViewById(R.id.email_login)
        passwordEditText = findViewById(R.id.password_login)
        button = findViewById(R.id.button_login)
        backregisterTextView = findViewById(R.id.back_login)

        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                emailEditText.hint = ""
            } else {
                emailEditText.hint = "Email"
            }
        }

        passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                passwordEditText.hint = ""
            } else {
                passwordEditText.hint = "Password"
            }
        }

        button.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }

            Log.d("MainActivity", "Log in with email: $email")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("MainActivity", "Successfully logged in: ${it.result!!.user?.uid}")

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
//                    overridePendingTransition(R.anim.enter, R.anim.exit)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        backregisterTextView.setOnClickListener {
            finish()
        }
    }
}