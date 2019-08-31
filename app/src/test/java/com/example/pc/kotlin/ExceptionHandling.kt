package com.example.pc.kotlin

import kotlinx.coroutines.*
import org.junit.Test
import java.io.IOException

/**
 *
 */
class ExceptionHandling {

    /**
     * 注意：异常的传播
     * 1.launch启动的协程中产生的异常无法被try-catch捕捉，async与launch相反
     * 2.前者对待异常是不处理的，类似于 Java 的 Thread.uncaughtExceptionHandler, 而后者依赖用户来最终消耗异常
     */
    @Test
    fun main1() = runBlocking {
        //GlobalScope.launch 协程产生的异常不影响runBlocking协程
        try {
            val job = GlobalScope.launch {
                println("Throwing exception from GlobalScope.launch")
                throw IndexOutOfBoundsException() // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
            }
            job.join()
            println("Joined failed GlobalScope.job")
        } catch (e: IndexOutOfBoundsException) {
            println("GlobalScope  Caught IndexOutOfBoundsException")
        }

        //如果打开注释，会直接报异常导致后面的代码无法执行到
//        try {
//            val job = launch {
//                println("Throwing exception from launch")
//                throw IndexOutOfBoundsException() // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
//            }
//            job.join()
//            println("Joined failed job")
//        } catch (e: IndexOutOfBoundsException) {
//            println("Caught IndexOutOfBoundsException")
//        }

        val deferred = GlobalScope.async {
            println("Throwing exception from async")
            throw ArithmeticException() // Nothing is printed, relying on user to call await
        }
        try {
            deferred.await()
            println("Unreached")
        } catch (e: ArithmeticException) {
            println("Caught ArithmeticException")
        }
    }

    /**
     * 在 JVM 中可以重定义一个全局的异常处理者来将所有的协程通过 ServiceLoader 注册到 CoroutineExceptionHandler
     * 全局异常处理者就如同 Thread.defaultUncaughtExceptionHandler 一样，在没有更多的指定的异常处理者被注册的时候被使用
     * 全局异常处理只适合GlobalScope下的相关协程构建器方法
     */
    @Test
    fun main2() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val job = GlobalScope.launch(handler) {
            println("GlobalScope.launch")
            throw AssertionError()
        }
        val deferred = GlobalScope.async(handler) {
            println("GlobalScope.async")
            throw ArithmeticException() // 没有打印任何东西，依赖用户去调用 deferred.await()
        }
//        joinAll(job, deferred)
    }

    /**
     *取消与异常
     * 1.协程是通过异常来达到取消的目的,父协程取消时地柜取消其子协程
     * 2.取消异常不能被try-catch，也不会被全局异常处理者捕获,但可以通过try-finally捕捉处理
     */
    @Test
    fun main3() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        val job = launch(handler) {
            val child = launch(handler) {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    println("Child is cancelled")
                }
            }
            println("Cancelling child")
            delay(1000)
            child.cancelAndJoin()

            println("Parent is not cancelled")
        }
    }

    /**
     * 1.如果协程遇到除 CancellationException 以外的异常，它将取消具有该协程的父协程，然后递归取消了其他子协程，
     * 因而被用来提供一个稳定的协程层次结构来进行结构化并发而无需依赖 CoroutineExceptionHandler 的实现
     * 2.
     */
    @Test
    fun main4() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        try {
            val job = GlobalScope.launch(handler) {
                launch {
                    // 第一个子协程
                    try {
                        delay(Long.MAX_VALUE)//挂起函数抛出取消异常
                    } finally {
                        withContext(NonCancellable) {
                            println("Children are cancelled, but exception is not handled until all children terminate")
                            delay(100)
                            println("The first child finished its non cancellable block")
                        }
                    }
                }
                launch(handler) {
                    // 第二个子协程
                    delay(10)
                    println("Second child throws an exception")
                    throw ArithmeticException()
                }
            }
            job.join()
            println("parent coroutine will be cancel")
        } finally {
            println("父协程因为子协程抛出非CancellationException而被取消了")
        }
        println("runBlocking is over !!!")
    }

    /**
     * 异常聚合
     * 1.如果一个协程的多个子协程抛出异常将会发生什么？ 通常的规则是 “第一个异常赢得了胜利”，所以第一个被抛出的异常将会暴露给处理者。 但也许这会是异常丢失的原因
     */
    @Test
    fun main5() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception with suppressed ${exception.suppressed.contentToString()}")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw ArithmeticException()
                }
            }
            launch {
                delay(100)
                throw IOException()
            }
            try {
                launch {
                    delay(150)
                    throw IndexOutOfBoundsException()
                }
            } catch (e: IndexOutOfBoundsException) {
                println(222)
            }

            delay(Long.MAX_VALUE)
        }
        job.join()
    }

    /**
     * 取消异常是透明的并且会在默认情况下解包？？？？
     * 因为如果协程遇到除 CancellationException 以外的异常，它将取消具有该协程的父协程
     */
    @Test
    fun main6() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught original $exception")
        }
        val job = GlobalScope.launch(handler) {
            val inner = launch {
                launch {
                    launch {
                        throw IOException()
                    }
                }
            }

            try {
                inner.join()
            } catch (e: CancellationException) {
                println("Rethrowing CancellationException with original cause")
                throw e
            }
        }
        job.join()
    }

    /**
     * 取消是一种双向机制，在协程的整个层次结构之间传播。但是如果需要单向取消怎么办????
     */

    /**
     *
     */
    @Test
    fun main7() = runBlocking {
        val supervisor = SupervisorJob()
        with(CoroutineScope(coroutineContext + supervisor)) {
            // 启动第一个子作业——这个示例将会忽略它的异常（不要在实践中这么做！）
            val firstChild = launch(CoroutineExceptionHandler { _, _ ->  }) {
                println("First child is failing")
                throw AssertionError("First child is cancelled")
            }
            // 启动第两个子作业
            val secondChild = launch {
                firstChild.join()
                // 取消了第一个子作业且没有传播给第二个子作业
                println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    // 但是取消了监督的传播
                    println("Second child is cancelled because supervisor is cancelled")
                }
            }
            // 等待直到第一个子作业失败且执行完成
            firstChild.join()
            println("Cancelling supervisor")
            supervisor.cancel()
            secondChild.join()
        }
    }

    /**
     *
     */
    @Test
    fun main8() = runBlocking {
        try {
            supervisorScope {
                val child = launch {
                    try {
                        println("Child is sleeping")
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("Child is cancelled")
                    }
                }
                // 使用 yield 来给我们的子作业一个机会来执行打印
                yield()
                println("Throwing exception from scope")
                throw AssertionError()
            }
        } catch(e: AssertionError) {
            println("Caught assertion error")
        }
    }

    /**
     *
     */
    @Test
    fun main9() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        supervisorScope {
            val child = launch(handler) {
                println("Child throws an exception")
                throw AssertionError()
            }
            println("Scope is completing")
        }
        println("Scope is completed")
    }
}