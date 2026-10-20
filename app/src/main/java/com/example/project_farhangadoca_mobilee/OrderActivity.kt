package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    companion object {
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_IMAGE = "extra_product_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbarOrder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarOrder.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME)
        val productDesc = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION)
        val productPrice = intent.getIntExtra(EXTRA_PRODUCT_PRICE, 0)
        val productImage = intent.getIntExtra(EXTRA_PRODUCT_IMAGE, R.drawable.busta)

        if (productName != null) {
            binding.tvProductName.text = productName
            binding.tvProductDescription.text = productDesc
            binding.tvProductPrice.text = "Rp %,d".format(productPrice).replace(',', '.')
            binding.ivProduct.setImageResource(productImage)
        } else {
            binding.tvProductName.text = "Keranjang Pesanan"
            binding.tvProductDescription.text = "Belum ada produk yang dipilih."
            binding.tvProductPrice.text = "Rp 0"
        }

        binding.btnConfirmOrder.setOnClickListener {
            Toast.makeText(this, "Pesanan berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BerandaActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
