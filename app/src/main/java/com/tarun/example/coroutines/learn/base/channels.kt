package com.tarun.example.coroutines.learn.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tarun.example.coroutines.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

class channels : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channels)
        Log.e("test","channels")
        main()

        /**
        val rendezvousChannel = Channel<String>()
        val bufferedChannel = Channel<String>(10)
        val conflatedChannel = Channel<String>(CONFLATED)
        val unlimitedChannel = Channel<String>(UNLIMITED)
         **/
    }

    fun main() = runBlocking {
        val channel = Channel<String>(CONFLATED)
        launch {
            channel.send("A1")
            channel.send("A2")
            log("A done")
        }
        launch {
            channel.send("B1")
            log("B done")
        }
        launch {
            repeat(6) {
                val x = channel.receive()
                log(x)
            }
        }
    }

    fun log(message: Any?) {
        Log.e("channels",Thread.currentThread().name+": "+message)
    }
}