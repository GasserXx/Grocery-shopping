package com.umega.grocery

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserPreference private constructor(private val context: Context) {

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
        return userFlow.first()
    }

    suspend fun storeEmail(email:String) {
        Log.i(TAG, "StoreEmail got called")
        context.dataStore.edit { settings ->
            settings[emailKey] = email
        }
    }
    private val emailFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[emailKey]
        }
    suspend fun getEmail(): String {
        Log.i(TAG, "emailFlow : $emailFlow")
        val result:String? = emailFlow.first()
        return if (result.isNullOrBlank()) "" else result
    }

    suspend fun clearPreference(){
        storeUserID(-1)
        storeEmail("")
    }



    companion object {

        const val TAG = "USER PREFERENCE CLASS"
        private var instance: UserPreference? = null

        // Initialize the singleton instance with a context
        fun initialize(context: Context) {
            if (instance == null) {
                instance = UserPreference(context)
            }
        }

        // Access the singleton instance
        fun getInstance(): UserPreference {
            return instance
                ?: throw IllegalStateException("UserPreference has not been initialized.")
        }
    }
}