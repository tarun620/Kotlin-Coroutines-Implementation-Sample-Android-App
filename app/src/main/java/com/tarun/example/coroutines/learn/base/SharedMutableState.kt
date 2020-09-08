package com.tarun.example.coroutines.learn.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

import kotlin.system.measureTimeMillis

class SharedMutableState : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("test","shared mutable state")
        normal()
//        usingThreadSafeDS()
//        usingMutexImplementation()
    }

    suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // number of coroutines to launch
        val k = 1000 // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
//        println("Completed ${n * k} actions in $time ms")
        Log.e("time:","Completed ${n * k} actions in $time ms")
    }
    val mutex = Mutex()

    var counter1 = 0
    val counter2 =AtomicInteger()
    var counter3=0

    fun normal() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter1++
            }
        }
        Log.e("normal: ","Counter1 = $counter1")
    }

    fun usingThreadSafeDS() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter2.incrementAndGet()
            }
        }
        Log.e("usingThreadSafeDS: ","Counter2 = $counter2")

    }

    fun usingMutexImplementation() = runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                // protect each increment with lock
                mutex.withLock {
                    counter3++
                }
            }
        }
        Log.e("usingMutexImplementation: ","Counter3 = $counter3")

    }
}