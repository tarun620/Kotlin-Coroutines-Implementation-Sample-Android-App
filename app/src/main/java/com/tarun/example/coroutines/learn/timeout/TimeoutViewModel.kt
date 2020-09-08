package com.tarun.example.coroutines.learn.timeout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.local.DatabaseHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class TimeoutViewModel(
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
                withTimeout(100) {
                    val usersFromApi = apiHelper.getUsers()
                    users.postValue(Resource.success(usersFromApi))
                }
            } catch (e: TimeoutCancellationException) {
                users.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                users.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}