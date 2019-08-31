package com.example.pc.kotlin

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutinesCancellationAndTimeOutTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun main1() = runBlocking {
        // this: CoroutineScope
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

    /**
     * job.join() 等待直到子协程执行结束
     */
    @Test
    fun main2() = runBlocking {
        //在 GlobalScope 中启动的活动协程并不会使进程保活。它们就像守护线程。
        val job = GlobalScope.launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        job.join() // 等待直到子协程执行结束,如果注释这行，将不会打印"World!"
    }

    /**
     * 我们可以在这个作用域中启动协程而无需显式 join 之，因为外部协程（示例中的 runBlocking）直到在其作用域中启动的所有协程都执行完毕后才会结束
     * 注意与main2对比
     */
    @Test
    fun main3() = runBlocking {
        launch {
            // 在 runBlocking 作用域中启动一个新协程
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }

    @Test
    fun main4() = runBlocking {
        GlobalScope.launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,") // 主线程中的代码会立即执行

        coroutineScope {
            // 创建一个协程作用域 但这个协程作用域会阻塞主协程
            println("coroutineScope 的子协程不阻塞主协程---开始")
            //launch 子协程不阻塞coroutineScope
            launch {
                delay(500L)
                println("coroutineScope --- launch")
            }
            println("coroutineScope 的子协程不阻塞主协程----结束")
        }
    }

    /**
     * 取消协程
     */
    @Test
    fun main5() = runBlocking {
        val job = launch {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancel() // 取消该作业
//        job.join() // 等待作业执行结束，不需要显示调用
//        job.cancelAndJoin()//合并了对 cancel 以及 join 的调用
        println("main: Now I can quit.")
    }


    /**
     * 取消是协作的???
     *  所有 kotlinx.coroutines 中的挂起函数都是 可被取消的 。
     *  它们检查协程的取消， 并在取消时抛出 CancellationException。 然而，如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的
     *
     */
    @Test
    fun main6() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
                // 每秒打印消息两次
                if (System.currentTimeMillis() >= nextPrintTime) {
//                    yield()//取消此注释后则会检查取消异常
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
//        job.cancel()//与调用cancelAndJoin()是有区别的
//        job.join()
        println("main: Now I can quit.")
    }

    /**
     * 协程内检查协程是否有被取消
     * 1.isActive 是一个可以被使用在 CoroutineScope 中的扩展属性
     * 2.在协程内调用yield()检查
     */
    @Test
    fun main7() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) { //
                // 每秒打印消息两次
                if (System.currentTimeMillis() >= nextPrintTime) {
                    yield()
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
//        job.cancel()//与调用cancelAndJoin()是有区别的
//        job.join()
        println("main: Now I can quit.")
    }


    /**
     * 协程取消后释放资源
     *1.try {……} finally {……} 表达式
     *
     *2.Kotlin 的 use 函数一般在协程被取消的时候执行它们的终结动作：
     */
    @Test
    fun main8() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
//        job.cancel()//与调用cancelAndJoin()是有区别的
//        job.join()
        println("main: Now I can quit.")
    }

    /**
     * 挂起一个被取消的协程
     */
    @Test
    fun main9() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
//                withContext(NonCancellable) {
                println("job: I'm running finally")
                delay(1000L)
                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
//                }
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        println("main: Now I can quit.")
    }

    /**
     * 协程超时
     * 1.不处理会报TimeoutCancellationException
     * 2.withTimeout 通过try-catch处理
     * 3.withTimeoutOrNull()超时返回null
     */
    @Test
    fun main10() = runBlocking {
        //        withTimeout(1300L) {
//            repeat(1000) { i ->
//                println("I'm sleeping $i ...")
//                delay(500L)
//            }
//        }

        try {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("timeout......")
        }

        val result = withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }

        println("result is $result")
    }


}
