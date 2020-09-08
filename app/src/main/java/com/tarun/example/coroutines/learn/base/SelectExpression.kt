package com.tarun.example.coroutines.learn.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*


class SelectExpression : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("test","select expression")
        main()
    }
    fun CoroutineScope.fizz() = produce<String> {
        while (true) { // sends "Fizz" every 300 ms
            delay(300)
            send("Fizz")
        }
    }

    fun CoroutineScope.buzz() = produce<String> {
        while (true) { // sends "Buzz!" every 500 ms
            delay(500)
            send("Buzz!")
        }
    }

    suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) {
        select<Unit> { // <Unit> means that this select expression does not produce any result
            fizz.onReceive { value ->  // this is the first select clause
                Log.e("selectFizzBuzzFun: ","fizz -> '$value'")
            }
            buzz.onReceive { value ->  // this is the second select clause
                Log.e("selectFizzBuzzFun: ","buzz -> '$value'")
            }
        }
    }

    fun main() = runBlocking<Unit> {
        val fizz = fizz()
        val buzz = buzz()
        repeat(7) {
            selectFizzBuzz(fizz, buzz)
        }
        coroutineContext.cancelChildren() // cancel fizz & buzz coroutines
    }

//    @kotlinx.coroutines.ObsoleteCoroutinesApi
//    suspend fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String =
//        select<String> {
//
//            a.onReceiveOrNull { value ->
//                if (value == null)
//                    "Channel 'a' is closed"
//                else
//                    "a -> '$value'"
//            }
//            b.onReceiveOrNull { value ->
//                if (value == null)
//                    "Channel 'b' is closed"
//                else
//                    "b -> '$value'"
//            }
//        }

}