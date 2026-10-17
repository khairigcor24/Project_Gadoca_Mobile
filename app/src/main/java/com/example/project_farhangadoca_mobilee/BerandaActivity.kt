package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityBerandaBinding

class BerandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bottom Navigation: Click Menu Tab -> Navigate to MenuActivity
        binding.tabMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Bottom Navigation: Click Akun Tab -> Navigate to DeveloperProfileActivity
        binding.tabAkun.setOnClickListener {
            val intent = Intent(this, DeveloperProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tabPesanan.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
    }
}
