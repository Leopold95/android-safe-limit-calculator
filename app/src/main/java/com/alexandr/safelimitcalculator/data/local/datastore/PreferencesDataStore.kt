package com.alexandr.safelimitcalculator.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alexandr.safelimitcalculator.data.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesDataStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val userDataFlow: Flow<UserData> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[USER_DATA_KEY]

        if (jsonString.isNullOrEmpty()) {
            UserData(0.0, java.time.LocalDate.now(), 0.0)
        } else {
            try {
                json.decodeFromString<UserData>(jsonString)
            } catch (_: Exception) {
                UserData(0.0, java.time.LocalDate.now(), 0.0)
            }
        }
    }

    val onboardingShownFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_SHOWN_KEY] ?: false
    }

    val paymentRemindersEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PAYMENT_REMINDERS_ENABLED_KEY] ?: false
    }

    val limitWarningsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LIMIT_WARNINGS_ENABLED_KEY] ?: false
    }

    val dailySummaryEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DAILY_SUMMARY_ENABLED_KEY] ?: false
    }

    suspend fun saveUserData(userData: UserData) {
        context.dataStore.edit { preferences ->
            preferences[USER_DATA_KEY] = json.encodeToString(userData)
        }
    }

    suspend fun setOnboardingShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_SHOWN_KEY] = shown
        }
    }

    suspend fun setPaymentRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PAYMENT_REMINDERS_ENABLED_KEY] = enabled
        }
    }

    suspend fun setLimitWarningsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LIMIT_WARNINGS_ENABLED_KEY] = enabled
        }
    }

    suspend fun setDailySummaryEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_SUMMARY_ENABLED_KEY] = enabled
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val ONBOARDING_SHOWN_KEY = booleanPreferencesKey("onboarding_shown")
        private val PAYMENT_REMINDERS_ENABLED_KEY = booleanPreferencesKey("payment_reminders_enabled")
        private val LIMIT_WARNINGS_ENABLED_KEY = booleanPreferencesKey("limit_warnings_enabled")
        private val DAILY_SUMMARY_ENABLED_KEY = booleanPreferencesKey("daily_summary_enabled")
    }
}
