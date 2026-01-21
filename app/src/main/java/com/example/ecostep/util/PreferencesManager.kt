package com.example.ecostep.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "ecostep_prefs"
    
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_DAILY_REMINDER = "daily_reminder"
    private const val KEY_WEEKLY_REPORT = "weekly_report"
    private const val KEY_PROFILE_PHOTO_URI = "profile_photo_uri"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun isDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DARK_MODE, false)
    }
    
    fun setDarkMode(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    
    fun areNotificationsEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
    
    fun isDailyReminderEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DAILY_REMINDER, true)
    }
    
    fun setDailyReminderEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DAILY_REMINDER, enabled).apply()
    }
    
    fun isWeeklyReportEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_WEEKLY_REPORT, false)
    }
    
    fun setWeeklyReportEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_WEEKLY_REPORT, enabled).apply()
    }
    
    fun getProfilePhotoUri(context: Context): String? {
        return getPrefs(context).getString(KEY_PROFILE_PHOTO_URI, null)
    }
    
    fun saveProfilePhotoUri(context: Context, uri: String) {
        getPrefs(context).edit().putString(KEY_PROFILE_PHOTO_URI, uri).apply()
    }
}

