package com.tarun.example.coroutines.learn.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tarun.example.coroutines.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

class AsynchronousFlow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_layout)

        flowListReturn()
        flowFirstExample()
        flowWithTimeOut()
        flowMapExample()
        flowSizeLimitOperator()
        flowReduceOperator()
        flowContext()
        flowWrongEmission()
        flowOn()
        flowCollectLatest()
        flowTryCatch()
        flowCatchOperator()
        flowImperativeFinallyBlock()
        flowDeclarativeOnCompletion()
    }
    suspend fun flowListReturnUtil(): List<Int> {
        delay(1000) // pretend we are doing something asynchronous here
        return listOf(1, 2, 3)
    }

    fun flowListReturn()=runBlocking<Unit>  {
        flowListReturnUtil().forEach { value -> Log.e("flowListReturn: ",value.toString())}
        //This code outputs the same numbers(1,2,3), but it waits 100ms before printing each one.
    }

    fun flowFirstExampleUtil(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    fun flowFirstExample() = runBlocking<Unit> {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                Log.e("flowFirstExample: ","I'm not blocked $k")
                delay(100)
            }
        }
        // Collect the flow
        flowFirstExampleUtil().collect { value -> Log.e("flowFirstExample: ",value.toString()) }
    }
    fun flowWithTimeOutUtil(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100)
            Log.e("flowWithTimeOut: ","Emitting $i")
            emit(i)
        }
    }

    fun flowWithTimeOut() = runBlocking<Unit> {
        withTimeoutOrNull(250) { // Timeout after 250ms
            flowWithTimeOutUtil().collect { value -> Log.e("flowWithTimeOut: ",value.toString()) }
        }
        Log.e("flowWithTimeOut: ","Done")
    }

    suspend fun flowMapExampleUtil(request: Int): String {
        delay(1000) // imitate long-running asynchronous work
        return "response $request"
    }

    fun flowMapExample() = runBlocking<Unit> {
        (1..3).asFlow() // a flow of requests
            .map { request -> flowMapExampleUtil(request) }
            .collect { response -> Log.e("flowMapExample: ",response) }
    }

    fun flowSizeLimitOperatorUtil(): Flow<Int> = flow {
        try {
            emit(1)
            emit(2)
            Log.e("flowSizeLimitOperator: ","This line will not execute")
            emit(3)
        } finally {
            Log.e("flowSizeLimitOperator: ","Finally in numbers")
        }
    }

    fun flowSizeLimitOperator() = runBlocking<Unit> {
        flowSizeLimitOperatorUtil()
            .take(2) // take only the first two
            .collect { value -> Log.e("flowSizeLimitOperator: ",value.toString())}
    }

    fun flowReduceOperator() = runBlocking<Unit> {
        val sum = (1..5).asFlow()
            .map { it * it } // squares of numbers from 1 to 5
            .toList() //convert the seqquence into a list of numbers
//            .reduce { a, b -> a + b } // sum them (terminal operator)
        Log.e("flowReduceOperator: ",sum.toString())
    }
    fun log(msg: String) = Log.e("flowContext: ","[${Thread.currentThread().name}] $msg")

    fun flowContextUtil(): Flow<Int> = flow {
        log("Started simple flow")
        for (i in 1..3) {
            emit(i)
        }
    }

    fun flowContext() = runBlocking<Unit> {
        flowContextUtil().collect { value -> log("Collected $value") }

        /**
         * Since simple().collect is called from the main thread,
         * the body of simple's flow is also called in the main thread.
         */
    }
    fun flowWrongEmissionUtil(): Flow<Int> = flow {
        // The WRONG way to change context for CPU-consuming code in flow builder
        kotlinx.coroutines.withContext(Dispatchers.Default) {
            for (i in 1..3) {
                Thread.sleep(100) // pretend we are computing it in CPU-consuming way
                emit(i) // emit next value
            }
        }
    }

    fun flowWrongEmission() = runBlocking<Unit> {
//        flowWrongEmissionUtil().collect { value -> Log.e("flowWrongEmission: ",value.toString()) }

        //this code will produce an exception
    }

    fun flowOnutil(): Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100) // pretend we are computing it in CPU-consuming way
            log("Emitting $i")
            emit(i) // emit next value
        }
    }.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder

    fun flowOn() = runBlocking<Unit> {
        flowOnutil().collect { value ->
            log("Collected $value")
        }

        /**
         * Notice how flow { ... } works in the background thread, while collection happens in the main thread:

        Another thing to observe here is that the flowOn operator has changed the default sequential
        nature of the flow. Now collection happens in one coroutine ("coroutine#1") and
        emission happens in another coroutine ("coroutine#2") that is running in another thread
        concurrently with the collecting coroutine.
         */
    }
    fun flowCollectLatestUtil(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(100) // pretend we are asynchronously waiting 100 ms
            emit(i) // emit next value
        }
    }

    fun flowCollectLatest() = runBlocking<Unit> {
        val time = measureTimeMillis {
            flowCollectLatestUtil()
                .collectLatest { value -> // cancel & restart on the latest value
                    Log.e("flowCollectLatest: ","Collecting $value")
                    delay(300) // pretend we are processing it for 300 ms
                    Log.e("flowCollectLatest: ","Done $value")
                }
        }
        Log.e("flowCollectLatest: ","Collected in $time ms")

        /**
         * Since the body of collectLatest takes 300 ms, but new values are emitted every 100 ms,
         * we see that the block is run on every value, but completes only for the last value:
         */
    }

    fun flowTryCatchUtil(): Flow<Int> = flow {
        for (i in 1..3) {
            Log.e("flowTryCatch: ","Emitting $i")
            emit(i) // emit next value
        }
    }

    fun flowTryCatch() = runBlocking<Unit> {
        try {
            flowTryCatchUtil().collect { value ->
                Log.e("flowTryCatch: ",value.toString())

                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            Log.e("flowTryCatch: ","Caught $e")
        }
    }

    fun flowCatchOperatorUtil(): Flow<String> =
        flow {
            for (i in 1..3) {
                Log.e("flowCatchOperator: ","Emitting $i")
                emit(i) // emit next value
            }
        }
            .map { value ->
                check(value <= 1) { "Crashed on $value" }
                "string $value"
            }

    fun flowCatchOperator() = runBlocking<Unit> {
        flowCatchOperatorUtil()
            .catch { e -> emit("Caught $e") } // emit on exception
            .collect { value ->Log.e("flowCatchOperator: ",value) }
    }

    fun flowImperativeFinallyBlockUtil(): Flow<Int> = (1..3).asFlow()

    fun flowImperativeFinallyBlock() = runBlocking<Unit> {
        try {
            flowImperativeFinallyBlockUtil().collect { value ->Log.e("flowCatchOperator: ",value.toString()) }
        } finally {
            Log.e("flowImperativeFinallyBlock: ","Done")
        }
    }

    fun flowDeclarativeOnCompletionUtil(): Flow<Int> = (1..3).asFlow()

    fun flowDeclarativeOnCompletion() = runBlocking<Unit> {
        flowDeclarativeOnCompletionUtil()
            .onCompletion {Log.e("flowDeclarativeOnCompletion: ","Done") }
            .collect { value -> Log.e("flowDeclarativeOnCompletion: ",value.toString()) }
    }
}