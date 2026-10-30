package com.example.project_farhangadoca_mobilee

import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Login button click -> Validate credentials using 2 rules
        binding.btnLogin.setOnClickListener {
            val inputUsername = binding.etUser.text.toString().trim()
            val inputPassword = binding.etPassword.text.toString().trim()

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Username dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil data user dari SharedPreferences (tersimpan saat verifikasi OTP)
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedUsername = sharedPreferences.getString("KEY_USERNAME", "farhan_user")
            val savedPassword = sharedPreferences.getString("KEY_PASSWORD", "password123")

            // Aturan Autentikasi:
            // a. Jika input username == password, maka langsung Boleh Login.
            // b. Jika input username == username di SharedPreferences DAN input password == password di SharedPreferences, maka Boleh Login.
            val isRuleA = (inputUsername == inputPassword)
            val isRuleB = (inputUsername == savedUsername && inputPassword == savedPassword)

            if (isRuleA || isRuleB) {
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BerandaActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login Gagal! Username atau Password salah.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
