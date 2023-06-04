package com.mashimur.messagram.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mashimur.messagram.R
import com.mashimur.messagram.messages.LatestMessagesActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID
import android.view.View
import android.widget.*
import com.mashimur.messagram.models.User


class RegisterActivity : AppCompatActivity() {
    //private val TAG = "MainActivity"
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var button: Button
    private lateinit var haveAccountTextView: TextView
    private lateinit var button1: Button
    private lateinit var circlebutton: CircleImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        usernameEditText = findViewById(R.id.username_register)
        emailEditText = findViewById(R.id.email_register)
        passwordEditText = findViewById(R.id.password_register)
        button = findViewById(R.id.button)
        button1 = findViewById(R.id.select_photo_button)
        haveAccountTextView = findViewById(R.id.have_account_register)
        circlebutton = findViewById(R.id.select_photo_image_view)

        usernameEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                usernameEditText.hint = ""
            } else {
                usernameEditText.hint = "Name"
            }
        }

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

        // Ініціалізація FirebaseApp
        FirebaseApp.initializeApp(this)

        button.setOnClickListener {
            performRegister()
        }

        haveAccountTextView.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        button1.setOnClickListener {
            Log.d("MainActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            circlebutton.setImageBitmap(bitmap)
            button1.alpha = 0f
            button1.text = ""
        }
    }

    private fun performRegister() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is: $email")
        Log.d("MainActivity", "Password is: $password")

        // Реєстрація користувача в Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Main", "Successfully created with: ${task.result?.user?.uid}")
                    uploadImageToFirebaseStorage(task.result?.user?.uid ?: "")
                } else {
                    // Обробка помилки реєстрації
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        val message = "Invalid email format"
                        Log.d("RegisterActivity", "Failed to create user: $message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        val message = "Registration failed"
                        Log.d("RegisterActivity", "Failed to create user: $message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }



    private fun uploadImageToFirebaseStorage(s: String) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        selectedPhotoUri?.let { uri ->
            ref.putFile(uri)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity", "File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to upload image: ${it.message}")
                }
        } ?: run {
            Log.d("RegisterActivity", "No photo selected")
        }

    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditText.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}
