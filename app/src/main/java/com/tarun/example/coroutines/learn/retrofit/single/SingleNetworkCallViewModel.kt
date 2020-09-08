package com.tarun.example.coroutines.learn.retrofit.single

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class SingleNetworkCallViewModel(
    private val apiHelper: ApiHelper
//    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getUsers()
                users.postValue(Resource.success(usersFromApi))
            } catch (e: Exception) {
                users.postValue(Resource.error(e.toString(), null))
            }

        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}