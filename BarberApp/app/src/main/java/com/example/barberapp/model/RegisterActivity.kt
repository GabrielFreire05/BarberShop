package com.example.barberapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barberapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val allowedDomains = listOf("@gmail.com", "@outlook.com", "@hotmail.com")
            if (allowedDomains.none { email.endsWith(it) }) {
                Toast.makeText(this, "Por favor, use um e-mail válido (Gmail, Outlook, Hotmail).", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Atualiza o nome do usuário no perfil do Firebase
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)

                        user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                            if(verificationTask.isSuccessful) {
                                Toast.makeText(this, "Cadastro realizado! Verifique seu e-mail.", Toast.LENGTH_LONG).show()
                            }
                        }

                        // Desloga o usuário e o envia para a tela de login para que ele verifique o e-mail antes de entrar
                        auth.signOut()
                        finish()
                    } else {
                        Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}