package com.example.pc.kotlin.coroutine

import kotlinx.coroutines.*

/*fun main() {
    //GlobalScope启动的协程不受父协程的影响，即父协程被取消时，它依然正常执行
    GlobalScope.launch {
        // 在后台启动一个新的协程并继续，不会阻塞主线程
        delay(1000L)
        println("World!")
    }
    println("Hello,") // 主线程中的代码会立即执行

    runBlocking {
        // runBlocking阻塞了主线程
        println("runBlocking 阻塞了主线程---阻塞开始")
        //launch 子协程不阻塞runBlocking
        launch {
            delay(2000L)  // ……我们延迟 2 秒来保证 JVM 的存活
        }
        println("runBlocking 阻塞了主线程---阻塞结束")
    }
    println("runBlocking 阻塞结束")
}*/

fun main() = runBlocking {
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,") // 主线程中的代码会立即执行

    launch {
        delay(1000L)
        println("Come ")
    }
    println("I'm")

    coroutineScope {
        // 但这个协程作用域会阻塞主协程
        println("coroutineScope 的子协程不阻塞主协程---开始")
        //launch 子协程不阻塞coroutineScope
        launch {
            delay(2000L)
            println("coroutineScope --- launch")
        }
        println("coroutineScope 的子协程不阻塞主协程----结束")
    }
    println("blocking 结束!!!")
}

fun main1() = runBlocking { // this: CoroutineScope
    launch {
        delay(200L)
        println("Task from runBlocking")
    }

    // 创建一个协程作用域
    coroutineScope {
        launch {
            delay(1500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
    }

    println("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
}
