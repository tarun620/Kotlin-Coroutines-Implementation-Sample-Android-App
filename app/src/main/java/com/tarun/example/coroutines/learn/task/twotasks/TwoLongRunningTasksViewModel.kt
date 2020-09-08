package com.tarun.example.coroutines.learn.task.twotasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.example.coroutines.data.api.ApiHelper
import com.tarun.example.coroutines.data.local.DatabaseHelper
import com.tarun.example.coroutines.data.model.ApiUser
import com.tarun.example.coroutines.utils.Resource
import kotlinx.coroutines.*

class TwoLongRunningTasksViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val status = MutableLiveData<Resource<String>>()
    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    fun startLongRunningTask() {
        viewModelScope.launch {
            status.postValue(Resource.loading(null))
            try {
                // do long running tasks
                /** Note that if we just call await without first calling start on individual coroutines,
                 * this will lead to sequential behavior, since await starts the coroutine execution
                 *  and waits for its finish,which is not the intended use-case for laziness. **/
//                val resultOneDeferred = async(start=CoroutineStart.LAZY) { doLongRunningTaskOne() }

                val resultOneDeferred = async { doLongRunningTaskOne() }
                val resultTwoDeferred = async { doLongRunningTaskTwo() }
//                val resultOneDeferred = withContext(Dispatchers.Default) { doLongRunningTaskOne() }
//                val resultTwoDeferred = withContext(Dispatchers.Default) { doLongRunningTaskTwo() }
                val combinedResult = resultOneDeferred.await() + resultTwoDeferred.await()
//                val combinedResult=resultOneDeferred+resultTwoDeferred

                status.postValue(Resource.success("Task Completed : $combinedResult"))
            } catch (e: Exception) {
                status.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getStatus(): LiveData<Resource<String>> {
        return status
    }

    private suspend fun doLongRunningTaskOne() :String{
        delay(5000)
        return "One"
    }

    private suspend fun doLongRunningTaskTwo() :String{
        delay(5000)
        return "Two"
    }

}

