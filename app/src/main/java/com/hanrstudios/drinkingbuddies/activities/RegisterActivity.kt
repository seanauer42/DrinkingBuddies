package com.hanrstudios.drinkingbuddies.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hanrstudios.drinkingbuddies.R
import com.hanrstudios.drinkingbuddies.classes.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        login_textview_register.setOnClickListener {
            goToLoginPage()
        }

    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_registeractivity.text.toString()
        val username = username_registeractivity.text.toString()

        if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
            Toast.makeText(this, "Please enter text in email, username, and password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("register", "successfully created user")
                saveUserToFirebaseDatabase()
            }
            .addOnFailureListener {
                Log.d("register", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create User: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            username_registeractivity.text.toString()
        )

        ref.setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this, BrowseGamesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
