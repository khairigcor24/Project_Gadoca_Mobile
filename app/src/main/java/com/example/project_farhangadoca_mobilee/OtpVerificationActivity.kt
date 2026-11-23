package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.data.api.SupabaseClient
import com.example.project_farhangadoca_mobilee.data.model.UserRequestModel
import com.example.project_farhangadoca_mobilee.databinding.ActivityOtpVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                // Kirim data ke Supabase REST API (Tabel users di schema mobile)
                val userRequest = UserRequestModel(
                    nama = regName,
                    email = "$regUsername@gadoca.com",
                    password = regPassword,
                    no_hp = regPhone
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = SupabaseClient.apiService.registerUser(userRequest)
                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@OtpVerificationActivity, "Registrasi berhasil disimpan ke Supabase!", Toast.LENGTH_LONG).show()

                                val intent = Intent(this@OtpVerificationActivity, LoginActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Unknown error"
                            Log.e("SupabaseRegister", "Error Code: ${response.code()}, Body: $errorBody")
                            withContext(Dispatchers.Main) {
                                MaterialAlertDialogBuilder(this@OtpVerificationActivity)
                                    .setTitle("Gagal Simpan ke Supabase")
                                    .setMessage("Error ${response.code()}: $errorBody")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("SupabaseRegister", "Exception: ${e.message}")
                        withContext(Dispatchers.Main) {
                            MaterialAlertDialogBuilder(this@OtpVerificationActivity)
                                .setTitle("Koneksi Gagal")
                                .setMessage("Pesan: ${e.message}")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
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
