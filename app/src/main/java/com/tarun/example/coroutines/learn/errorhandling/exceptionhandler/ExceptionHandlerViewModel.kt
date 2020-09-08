package com.tarun.example.coroutines.learn.errorhandling.exceptionhandler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.local.DatabaseHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ExceptionHandlerViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        users.postValue(Resource.error("Something Went Wrong", null))
    }

    init {
        fetchUsers()
    }
/**we can handle an exception using a global coroutine exception handler called CoroutineExceptionHandler.**/
    private fun fetchUsers() {
        viewModelScope.launch(exceptionHandler) {
//            users.postValue(Resource.loading(null))
//            val usersFromApi = apiHelper.getUsers()
//            users.postValue(Resource.success(usersFromApi))

            val usersFromApi = apiHelper.getUsers()
            val moreUsersFromApi = apiHelper.getUsersWithError()

            val allUsersFromApi = mutableListOf<ApiUser>()
            allUsersFromApi.addAll(usersFromApi)
            allUsersFromApi.addAll(moreUsersFromApi)

            users.postValue(Resource.success(usersFromApi))
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}