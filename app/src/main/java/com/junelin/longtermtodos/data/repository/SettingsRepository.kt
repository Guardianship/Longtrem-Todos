package com.junelin.longtermtodos.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val WIDGET_DISPLAY_DAYS = intPreferencesKey("widget_display_days")
        val DEFAULT_REMIND_DAYS = intPreferencesKey("default_remind_days")
        val AUTO_EXTRACT_SMS = booleanPreferencesKey("auto_extract_sms")
        val AUTO_EXTRACT_WECHAT = booleanPreferencesKey("auto_extract_wechat")
        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val THEME_COLOR = stringPreferencesKey("theme_color")
    }

    val widgetDisplayDays: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[WIDGET_DISPLAY_DAYS] ?: 14
    }

    val defaultRemindDays: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_REMIND_DAYS] ?: 3
    }

    val autoExtractSms: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_EXTRACT_SMS] ?: true
    }

    val autoExtractWechat: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_EXTRACT_WECHAT] ?: true
    }

    val biometricLock: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_LOCK] ?: false
    }

    val themeColor: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[THEME_COLOR]
    }

    suspend fun setWidgetDisplayDays(days: Int) {
        context.dataStore.edit { prefs ->
            prefs[WIDGET_DISPLAY_DAYS] = days
        }
    }

    suspend fun setDefaultRemindDays(days: Int) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_REMIND_DAYS] = days
        }
    }

    suspend fun setAutoExtractSms(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_EXTRACT_SMS] = enabled
        }
    }

    suspend fun setAutoExtractWechat(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_EXTRACT_WECHAT] = enabled
        }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_LOCK] = enabled
        }
    }

    suspend fun setThemeColor(color: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_COLOR] = color
        }
    }
}
