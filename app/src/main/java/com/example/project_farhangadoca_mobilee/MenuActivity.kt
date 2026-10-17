package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bottom Navigation: Click Beranda Tab -> Navigate to BerandaActivity
        binding.tabBeranda.setOnClickListener {
            val intent = Intent(this, BerandaActivity::class.java)
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

        val categoryChips = listOf(
            binding.chipSemua to "Semua",
            binding.chipKopi to "Kopi",
            binding.chipPastry to "Pastry",
            binding.chipMinuman to "Minuman"
        )
        categoryChips.forEach { (chip, category) ->
            chip.setOnClickListener {
                filterProducts(category, categoryChips)
            }
        }

        binding.addBusta.setOnClickListener {
            openOrder("Busta", "Kopi · Favorit pelanggan", 28000, R.drawable.busta)
        }
        binding.addBica.setOnClickListener {
            openOrder("Bica", "Kopi · Lembut & seimbang", 30000, R.drawable.bica)
        }
        binding.addButterscotch.setOnClickListener {
            openOrder("Butterscotch", "Pastry · Dipanggang harian", 24000, R.drawable.buter)
        }
        binding.addDarkChoco.setOnClickListener {
            openOrder("DarkChoco", "Minuman · Matcha premium", 25000, R.drawable.dark)
        }
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

    private fun filterProducts(
        category: String,
        categoryChips: List<Pair<TextView, String>>
    ) {
        val products = listOf(
            binding.productBusta to "Kopi",
            binding.productBica to "Kopi",
            binding.productButterscotch to "Pastry",
            binding.productDarkChoco to "Minuman"
        )
        products.forEach { (product, productCategory) ->
            product.visibility =
                if (category == "Semua" || category == productCategory) View.VISIBLE else View.GONE
        }

        updateProductRow(binding.productRowOne, listOf(binding.productBusta, binding.productBica))
        updateProductRow(
            binding.productRowTwo,
            listOf(binding.productButterscotch, binding.productDarkChoco)
        )
        val visibleCount = products.count { (product, _) -> product.visibility == View.VISIBLE }
        binding.tvMenuCount.text = "$visibleCount menu pilihan"

        categoryChips.forEach { (chip, chipCategory) ->
            val selected = chipCategory == category
            chip.setBackgroundResource(
                if (selected) R.drawable.bg_chip_active else R.drawable.bg_chip_inactive
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (selected) R.color.white else R.color.gadoca_text_dark
                )
            )
            chip.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun updateProductRow(row: LinearLayout, products: List<View>) {
        val visibleProducts = products.filter { it.visibility == View.VISIBLE }
        row.visibility = if (visibleProducts.isEmpty()) View.GONE else View.VISIBLE

        products.forEach { product ->
            val params = product.layoutParams as LinearLayout.LayoutParams
            if (visibleProducts.size == 1 && product.visibility == View.VISIBLE) {
                params.weight = 2f
                params.marginStart = 0
                params.marginEnd = 0
            } else {
                params.weight = 1f
                params.marginStart = if (product == products.last()) dp(8) else 0
                params.marginEnd = if (product == products.first()) dp(8) else 0
            }
            product.layoutParams = params
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
