package com.tarun.example.coroutines.learn.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class WithContextLaunchAsync : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("test","withContext")
        main()
    }

    //runBlocking is the way to bridge synchronous and asynchronous code
    //runBlocking is almost never a tool you use in production.
    // It undoes the asynchronous, non-blocking nature of coroutines.

    fun main() = runBlocking {
        var resultOne = "Hardstyle"
        var resultTwo = "Minions"
        Log.e("withContext", "Before")
        resultOne = withContext(Dispatchers.IO) { function1() }
        resultTwo = withContext(Dispatchers.IO) { function2() }
        Log.e("withContext", "After")
        val resultText = resultOne + resultTwo
        Log.e("withContext", resultText)
    }
    suspend fun function1(): String {
        delay(1000L)
        val message = "function1"
        Log.e("withContext", message)
        return message
    }
    suspend fun function2(): String {
        delay(100L)
        val message = "function2"
        Log.e("withContext", message)
        return message
    }

}