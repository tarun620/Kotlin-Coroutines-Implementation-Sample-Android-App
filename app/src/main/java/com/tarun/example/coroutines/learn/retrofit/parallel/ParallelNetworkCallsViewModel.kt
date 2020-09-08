package com.tarun.example.coroutines.learn.retrofit.parallel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Api
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.local.DatabaseHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ParallelNetworkCallsViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            try {
                val usersFromApiDeferred = async { apiHelper.getUsers() }
                val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }
                val usersFromApi = usersFromApiDeferred.await()
                val moreUsersFromApi = moreUsersFromApiDeferred.await()

                val allUsersFromApi = mutableListOf<ApiUser>()
                allUsersFromApi.addAll(usersFromApi)
                allUsersFromApi.addAll(moreUsersFromApi)

                users.postValue(Resource.success(allUsersFromApi))
            } catch (e: Exception) {
                users.postValue(Resource.error("Something Went Wrong", null))
            }

        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}