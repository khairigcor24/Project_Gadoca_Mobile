package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityOtpVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data registrasi yang dikirim dari RegisterActivity (dengan fallback default)
        val regName = intent.getStringExtra("EXTRA_NAME") ?: "Farhan Gadoca"
        val regPhone = intent.getStringExtra("EXTRA_PHONE") ?: "08123456789"
        val regUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "farhan_user"
        val regPassword = intent.getStringExtra("EXTRA_PASSWORD") ?: "password123"

        binding.btnVerify.setOnClickListener {
            val enteredOtp = binding.etOtp.text.toString().trim()

            // Validasi: Jika kode OTP yang dimasukkan SAMA dengan nomor handphone registrasi
            if (enteredOtp.isNotEmpty() && enteredOtp == regPhone) {
                // Verifikasi sukses: Simpan data user ke SharedPreferences
                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putString("KEY_NAME", regName)
                    putString("KEY_PHONE", regPhone)
                    putString("KEY_USERNAME", regUsername)
                    putString("KEY_PASSWORD", regPassword)
                    putBoolean("KEY_IS_VERIFIED", true)
                    apply()
                }

                Toast.makeText(this, "Verifikasi OTP Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()

                // Arahkan kembali ke Halaman Login (LoginActivity)
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                // Jika OTP berbeda atau kosong, tampilkan error menggunakan MaterialAlertDialog
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage("Kode OTP yang Anda masukkan salah atau kosong. Masukkan nomor handphone registrasi Anda ($regPhone).")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
