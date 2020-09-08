package com.tarun.example.coroutines.learn.task.onetask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.local.DatabaseHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.*

class LongRunningTaskViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val status = MutableLiveData<Resource<String>>()

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    init {
        startLongRunningTask()
    }

    private fun startLongRunningTask() {
        viewModelScope.launch {
//            status.postValue(Resource.loading(null))
            users.postValue(Resource.loading(null))
            try {
                // do a long running task
                doLongRunningTask()
//                status.postValue(Resource.success("Task Completed"))
            } catch (e: Exception) {
//                status.postValue(Resource.error("Something Went Wrong", null))
                users.postValue(Resource.error(e.toString(), null))

            }
        }
    }

//    fun getStatus(): LiveData<Resource<String>> {
//        return status
//    }


    private suspend fun doLongRunningTask() {
//        withContext(Dispatchers.Default){
//            // your code for doing a long running task
//            // Added delay to simulate
//            delay(5000)
//        }
        val usersFromApi = apiHelper.getUsers()
        users.postValue(Resource.success(usersFromApi))
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}

// withContext

/**
fun testWithContext {
    var resultOne = "Hardstyle"
    var resultTwo = "Minions"
    Log.i("withContext", "Before")
    resultOne = withContext(Dispatchers.IO) { function1() }
    resultTwo = withContext(Dispatchers.IO) { function2() }
    Log.i("withContext", "After")
    val resultText = resultOne + resultTwo
    Log.i("withContext", resultText)
}
suspend fun function1(): String {
    delay(1000L)
    val message = "function1"
    Log.i("withContext", message)
    return message
}
suspend fun function2(): String {
    delay(100L)
    val message = "function2"
    Log.i("withContext", message)
    return message
}

withContext: Before
withContext: function1
withContext: function2
withContext: After
withContext: function1function2
 **/



// launch

/**
fun testLaunch {
    var resultOne = "Hardstyle"
    var resultTwo = "Minions"
    Log.i("Launch", "Before")
    launch(Dispatchers.IO) {resultOne = function1() }
    launch(Dispatchers.IO) {resultTwo = function2() }
    Log.i("Launch", "After")
    val resultText = resultOne + resultTwo
    Log.i("Launch", resultText)
}
suspend fun function1(): String {
    delay(1000L)
    val message = "function1"
    Log.i("Launch", message)
    return message
}
suspend fun function2(): String {
    delay(100L)
    val message = "function2"
    Log.i("Launch", message)
    return message
}

Launch: Before
Launch: After
Launch: HardstyleMinions //don't wait for results
Launch: function2   // 2 first
Launch: function1
 **/



//async

/**

fun testAsync {
    Log.i("Async", "Before")
    val resultOne = Async(Dispatchers.IO) { function1() }
    val resultTwo = Async(Dispatchers.IO) { function2() }
    Log.i("Async", "After")
    val resultText = resultOne.await() + resultTwo.await()
    Log.i("Async", resultText)
}
suspend fun function1(): String {
    delay(1000L)
    val message = "function1"
    Log.i("Async", message)
    return message
}
    suspend fun function2(): String {
    delay(100L)
    val message = "function2"
    Log.i("Async", message)
    return message
}

Async: Before
Async: After
Async: function2 //2 first
Async: function1
Async: function1function2 //wait for results and block.

 **/