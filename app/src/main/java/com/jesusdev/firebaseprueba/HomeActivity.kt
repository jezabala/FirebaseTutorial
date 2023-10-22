package com.jesusdev.firebaseprueba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC
}

class HomeActivity : AppCompatActivity() {
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView
    private lateinit var tvRol: TextView
    private lateinit var logOutButton: Button
    private lateinit var getDataButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        initComponents()
        setup(email ?: "")

    }

    private fun initComponents(){
        tvUsername = findViewById(R.id.userTextView)
        tvEmail = findViewById(R.id.emailTextView)
        tvPassword = findViewById(R.id.passwordTextView)
        tvRol = findViewById(R.id.rolTextView)
        logOutButton = findViewById(R.id.logOutButton)
        getDataButton = findViewById(R.id.getButton)
    }

    private fun setup(email: String){
        title = "Inicio"

        tvEmail.text = email

        getDataButton.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                tvUsername.text = it.get("username") as String?
                tvPassword.text = it.get("password") as String?
                tvRol.text = it.get("rol") as String?
            }
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressedDispatcher.onBackPressed()
        }

    }
}