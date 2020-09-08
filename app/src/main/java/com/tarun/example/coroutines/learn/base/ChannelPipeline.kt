package com.tarun.example.coroutines.learn.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tarun.example.coroutines.R
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class ChannelPipeline : AppCompatActivity() {
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_layout)
        Log.e("test","channel pipeline")
        main()
    }

    @ExperimentalCoroutinesApi
    fun main() = runBlocking {
        var cur = numbersFrom(2)
        repeat(10) {
            val prime = cur.receive()
            Log.e("prime",prime.toString())
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren() // cancel all children to let main finish
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
        var x = start
        while (true) send(x++) // infinite stream of integers from start
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
        for (x in numbers)
            if (x % prime != 0) send(x)
    }
}