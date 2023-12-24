package com.umega.grocery.dataBase

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.umega.grocery.UserPreference
import com.umega.grocery.dataBase.remote.Remote
import com.umega.grocery.utill.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking

@OptIn(DelicateCoroutinesApi::class)
class Repo(context: Context) {
    private val remote = Remote()
    private val localDatabase = LocalDatabaseHelper(context)

    fun authentication(user: User, response:MutableLiveData<Int>){
        runBlocking {
            response.value = remote.authentication(user)
        }
    }

    fun registerUser(user: User, response:MutableLiveData<Int>){
        runBlocking {
            response.value = remote.registerUser(user)
        }
    }

    fun storeUserID(userPreference: UserPreference){
        runBlocking {
            userPreference.storeUserID(remote.getUserID(userPreference.getEmail()))
        }
    }

}