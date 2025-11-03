package com.qaassist.inspection.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object SharedPrefsUtil {
    private const val PREFS_NAME = "com.qaassist.inspection.prefs"
    private const val KEY_SAVE_LOCATION = "save_location"
    private const val KEY_THEME = "theme"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setSaveLocation(context: Context, location: SaveLocation) {
        getPrefs(context).edit().putString(KEY_SAVE_LOCATION, location.name).apply()
    }

    fun getSaveLocation(context: Context): SaveLocation {
        val locationName = getPrefs(context).getString(KEY_SAVE_LOCATION, SaveLocation.DEVICE_STORAGE.name)
        return SaveLocation.valueOf(locationName ?: SaveLocation.DEVICE_STORAGE.name)
    }

    fun setTheme(context: Context, theme: ThemeMode) {
        getPrefs(context).edit().putString(KEY_THEME, theme.name).apply()
        applyTheme(theme)
    }

    fun getTheme(context: Context): ThemeMode {
        val themeName = getPrefs(context).getString(KEY_THEME, ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(themeName ?: ThemeMode.SYSTEM.name)
    }

    fun applyTheme(theme: ThemeMode) {
        when (theme) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeMode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}

enum class SaveLocation {
    DEVICE_STORAGE,
    SD_CARD
}