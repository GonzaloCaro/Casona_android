package com.example.casonaapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val EMAIL_KEY = stringPreferencesKey("email")
    private val PASSWORD_KEY = stringPreferencesKey("password")

    val userFlow: Flow<Pair<String, String>?> = context.dataStore.data
        .map { prefs ->
            val email = prefs[EMAIL_KEY]
            val password = prefs[PASSWORD_KEY]
            if (email != null && password != null) Pair(email, password) else null
        }

    suspend fun saveUser(email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[PASSWORD_KEY] = password
        }
    }
}