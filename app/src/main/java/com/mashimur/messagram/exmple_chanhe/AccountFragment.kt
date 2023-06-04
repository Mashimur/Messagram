package com.mashimur.messagram.exmple_chanhe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mashimur.messagram.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class AccountFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var selectPhotoButton: Button
    private lateinit var profileImageView: CircleImageView
    private var selectedPhotoUri: Uri? = null
    private var isEditMode = false
    private var isPhotoSelected = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        usernameEditText = view.findViewById(R.id.username_account)
        saveButton = view.findViewById(R.id.save_change_account)
        selectPhotoButton = view.findViewById(R.id.select_photo_button_account)
        profileImageView = view.findViewById(R.id.select_photo_image_view_account)

        // Set current username
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUsername = currentUser?.displayName
        usernameEditText.setText(currentUsername)

        // Set current profile photo
        val currentPhotoUrl = currentUser?.photoUrl
        if (currentPhotoUrl != null) {
            Picasso.get()
                .load(currentPhotoUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profileImageView)
            selectPhotoButton.alpha = 0f
            selectPhotoButton.text = ""
        }

        usernameEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                usernameEditText.hint = ""
            } else {
                usernameEditText.hint = "Name"
            }
        }

        selectPhotoButton.setOnClickListener {
            if (isEditMode) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }

        saveButton.setOnClickListener {
            if (isEditMode) {
                saveChanges()
            } else {
                isEditMode = true
                updateEditMode()
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            Log.d("AccountFragment", "Photo was selected")

            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedPhotoUri)
            profileImageView.setImageBitmap(bitmap)
            selectPhotoButton.alpha = 0f
            selectPhotoButton.text = ""

            // Оновити дані профілю
            updateProfileData(usernameEditText.text.toString(), selectedPhotoUri.toString())
        }
    }

    override fun onResume() {
        super.onResume()

        // Отримати оновлені дані користувача з Firebase
        // Оновлення інтерфейсу з даними профілю
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value as? String
                    val photoUrl = snapshot.child("profileImageUrl").value as? String

                    // Оновлення інтерфейсу з даними профілю
                    updateProfileData(username, photoUrl)

                    // Перевіряємо, чи вибране нове фото
                    if (isPhotoSelected) {
                        // Завантажити нове фото
                        Picasso.get()
                            .load(photoUrl)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(profileImageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обробка помилки
                }
            })
        }
    }
    private fun updateProfileData(username: String?, photoUrl: String?) {
        // Оновлення інтерфейсу з інформацією профілю
        usernameEditText.setText(username ?: "")

        if (photoUrl != null) {
            val imageUrlWithCacheBusting = "$photoUrl?${System.currentTimeMillis()}"
            Picasso.get()
                .load(imageUrlWithCacheBusting)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profileImageView)
            selectPhotoButton.alpha = 0f
            selectPhotoButton.text = ""
        }

        // Перевірити, чи вибране нове фото
        if (isPhotoSelected) {
            // Завантажити нове фото
            val selectedPhotoUrlWithCacheBusting = "$photoUrl?${System.currentTimeMillis()}"
            Picasso.get()
                .load(selectedPhotoUrlWithCacheBusting)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profileImageView)
        }

        updateEditMode()
    }
    private fun updateEditMode() {
        if (isEditMode) {
            usernameEditText.isEnabled = true
            selectPhotoButton.isEnabled = true
            saveButton.text = "Save"
        } else {
            usernameEditText.isEnabled = false
            selectPhotoButton.isEnabled = false
            saveButton.text = "Edit"
        }
    }

    private fun saveChanges() {
        val username = usernameEditText.text.toString()

        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a username", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPhotoUri == null) {
            // Only updating the username
            updateUsername(username)
            uploadPhotoAndUsername(username)
        } else {
            // Uploading photo and updating username
            uploadPhotoAndUsername(username)
        }
    }

    private fun updateUsername(username: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Оновлення імені користувача успішно
                    saveUsernameInDatabase(username)
                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()

                    // Оновити відображення імені користувача
                    usernameEditText.setText(username)

                    isEditMode = false
                    updateEditMode()
                } else {
                    Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadPhotoAndUsername(username: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$userId/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(uri)
                        .build()

                    val user = FirebaseAuth.getInstance().currentUser

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Оновлення фото профілю успішно
                                saveUsernameInDatabase(username)
                                savePhotoUrlInDatabase(uri.toString())
                                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                // Завантажити нове фото за допомогою Picasso
                                Picasso.get()
                                    .load(uri)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(profileImageView)

                                selectPhotoButton.text = ""
                            } else {
                                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUsernameInDatabase(username: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid)
            databaseReference.child("username").setValue(username)
        }
    }

    private fun savePhotoUrlInDatabase(photoUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid)
            databaseReference.child("profileImageUrl").setValue(photoUrl)
        }
    }
}