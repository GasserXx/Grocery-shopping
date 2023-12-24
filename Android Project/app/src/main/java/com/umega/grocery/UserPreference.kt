package com.umega.grocery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class UserPreference(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "UserPreference")
    private val userKey = intPreferencesKey("UserID")
    private val emailKey = stringPreferencesKey("UserEmail")

    //userID
    suspend fun storeUserID(userID:Int) {
        context.dataStore.edit { settings ->
            settings[userKey] = userID
        }
    }
    private val userFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[userKey] ?: 0
        }
    suspend fun getUser(): Int {
        var result = 0
        userFlow.collect { value ->
            result = value
        }
        return result
    }

    suspend fun storeEmail(email:String) {
        context.dataStore.edit { settings ->
            settings[emailKey] = email
        }
    }
    private val emailFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[emailKey]
        }
    suspend fun getEmail(): String {
        var result:String? = ""
        emailFlow.collect { value ->
            result = value
        }
        return if (result.isNullOrBlank())"" else result!!
    }
}