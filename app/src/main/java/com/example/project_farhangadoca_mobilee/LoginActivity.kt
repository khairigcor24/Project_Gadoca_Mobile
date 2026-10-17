package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarLogin) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Setup Toolbar with Back button (Up Navigation)
        setSupportActionBar(binding.toolbarLogin)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbarLogin.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Navigate to RegisterActivity when clicking "Belum punya akun? Daftar sekarang"
        binding.tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Login button click -> Validate credentials
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi kredensial (Username/Email dan Password)
            // Kredensial benar: username/email "farhan" dan password "farhan"
            if (email == "farhan" && password == "farhan") {
                sessionManager.setLoggedIn(true)
                val intent = Intent(this, BerandaActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Login salah! Username atau kata sandi salah.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
