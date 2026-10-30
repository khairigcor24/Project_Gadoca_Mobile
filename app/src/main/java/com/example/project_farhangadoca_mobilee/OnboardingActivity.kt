package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sessionManager: SessionManager
    private var currentIndex = 0

    private val titles = arrayOf(
        "Nikmati Kopi Terbaik",
        "Suasana & Sudut Nyaman",
        "Poin & Hadiah Eksklusif"
    )

    private val descriptions = arrayOf(
        "Nikmati biji kopi pilihan langsung dari petani lokal terbaik dengan racikan khas Gadoca.",
        "Temukan ruang tenang untuk bekerja, mengobrol santai, atau menikmati waktu sendiri.",
        "Kumpulkan poin di setiap kunjungan dan tukarkan dengan berbagai hadiah menarik."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateContent()

        binding.btnOnboardingNext.setOnClickListener {
            currentIndex++
            if (currentIndex < titles.size) {
                updateContent()
                if (currentIndex == titles.size - 1) {
                    binding.btnOnboardingNext.text = "Mulai Sekarang →"
                }
            } else {
                navigateToLogin()
            }
        }

        binding.tvSkip.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun updateContent() {
        binding.tvOnboardingTitle.text = titles[currentIndex]
        binding.tvOnboardingDesc.text = descriptions[currentIndex]
    }

    private fun navigateToLogin() {
        sessionManager.setOnboardingShown(true)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
