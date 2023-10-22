package com.jesusdev.firebaseprueba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var rolUser: EditText
    private lateinit var signUpButton: Button
    private lateinit var logInButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        initComponents()
        setup()
    }

    private fun initComponents() {
        username = findViewById(R.id.userEditText)
        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        rolUser = findViewById(R.id.rolEditText)
        signUpButton = findViewById(R.id.logOutButton)
        logInButton = findViewById(R.id.loginButton)
    }

    private fun setup() {
        title = "Autenticacion"

        signUpButton.setOnClickListener {
            if (username.text.isNotEmpty() && email.text.isNotEmpty() && password.text.isNotEmpty() &&
                rolUser.text.isNotEmpty()
            ) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("users").document(email.text.toString()).set(
                            hashMapOf(
                                "username" to username.text.toString(),
                                "email" to email.text.toString(),
                                "password" to password.text.toString(),
                                "rol" to rolUser.text.toString()
                            )
                        )
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        logInButton.setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {

        if (email != null) {
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        when (documentSnapshot.getString("rol")) {
                            "ADMIN" -> {
                                val adminHomeIntent = Intent(this, AdminActivity::class.java).apply {
                                    putExtra("email", email)
                                }
                                startActivity(adminHomeIntent)
                                finish()
                            }

                            "MEDICO" -> {
                                val medicoHomeIntent = Intent(this, MedicosActivity::class.java).apply {
                                    putExtra("email", email)
                                }
                                startActivity(medicoHomeIntent)
                                finish()
                            }

                            "USUARIO" -> {
                                val usuarioHomeIntent = Intent(this, HomeActivity::class.java).apply {
                                    putExtra("email", email)
                                }
                                startActivity(usuarioHomeIntent)
                                finish()
                            }

                            else -> Toast.makeText(this, "Usuario no valido", Toast.LENGTH_LONG)
                        }
                    }
                }.addOnFailureListener {
                Toast.makeText(this, "Error de autenticacion", Toast.LENGTH_LONG)
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG)
        }

        /*val homeIntent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)*/
    }

}