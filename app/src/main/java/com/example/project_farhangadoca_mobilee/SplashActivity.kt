package com.example.project_farhangadoca_mobilee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project_farhangadoca_mobilee.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // Delay 1.5 seconds then check session and onboarding
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                sessionManager.isLoggedIn() -> {
                    startActivity(Intent(this, BerandaActivity::class.java))
                    finish()
                }
                !sessionManager.isOnboardingShown() -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }, 1500)
    }
}
