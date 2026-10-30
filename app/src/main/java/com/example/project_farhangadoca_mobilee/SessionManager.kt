package com.example.project_farhangadoca_mobilee

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "GadocaPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ONBOARDING_SHOWN = "onboardingShown"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setOnboardingShown(shown: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_SHOWN, shown).apply()
    }

    fun isOnboardingShown(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_SHOWN, false)
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }
}
