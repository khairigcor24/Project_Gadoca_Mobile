package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

        val locale = Locale.forLanguageTag("id-ID")
        binding.tvHomeDate.text = DateFormat.getDateInstance(DateFormat.FULL, locale).format(Date())
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 4..10 -> "pagi"
            in 11..14 -> "siang"
            in 15..17 -> "sore"
            else -> "malam"
        }
        binding.tvGreeting.text = "Selamat $greeting!"

        binding.cardHomePromo.setOnClickListener { openMenu() }
        binding.tvBrowseMenu.setOnClickListener { openMenu() }
        binding.tvSeeAllFavorites.setOnClickListener { openMenu() }
        binding.ivNotification.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.addHomeBusta.setOnClickListener {
            openOrder("Busta", "Kopi · Manis dan lembut", 28000, R.drawable.busta)
        }
        binding.addHomeButterscotch.setOnClickListener {
            openOrder("Butterscotch", "Pastry · Renyah berlapis", 24000, R.drawable.buter)
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

    private fun openMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }

    private fun openOrder(name: String, description: String, price: Int, imageResId: Int) {
        startActivity(
            Intent(this, OrderActivity::class.java).apply {
                putExtra(OrderActivity.EXTRA_PRODUCT_NAME, name)
                putExtra(OrderActivity.EXTRA_PRODUCT_DESCRIPTION, description)
                putExtra(OrderActivity.EXTRA_PRODUCT_PRICE, price)
                putExtra(OrderActivity.EXTRA_PRODUCT_IMAGE, imageResId)
            }
        )
    }
}
