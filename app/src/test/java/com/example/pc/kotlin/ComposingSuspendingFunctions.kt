package com.example.pc.kotlin

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.system.measureTimeMillis

class ComposingSuspendingFunctions {
    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了一些有用的事
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了一些有用的事
        return 29
    }

    @Test
    fun main1() = runBlocking {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")
        }
        println("Completed in $time ms")
    }

    /**
     * 使用 async 并发
     * 1.使用协程进行并发总是显式的
     * 2.你可以使用 .await() 在一个延期的值上得到它的最终结果， 但是 Deferred 也是一个 Job，所以如果需要的话，你可以取消它
     *
     */
    @Test
    fun main2() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    /**
     * 惰性并发
     * 注意，如果我们只是在 println 中调用 await，而没有在单独的协程中调用 start，
     * 这将会导致顺序行为，直到 await 启动该协程 执行并等待至它结束，这并不是惰性的预期用例。
     *
     * 在计算一个值涉及挂起函数时，这个 async(start = CoroutineStart.LAZY) 的用例用于替代标准库中的 lazy 函数
     */
    @Test
    fun main3() = runBlocking<Unit> {
        val time = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            // 执行一些计算
//            one.start() // 启动第一个
//            two.start() // 启动第二个
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    /**
     * async 风格的函数
     * 这种带有异步函数的编程风格仅供参考,在 Kotlin 的协程中使用这种风格是强烈不推荐的
     * 考虑一下如果 val one = somethingUsefulOneAsync() 这一行和 one.await() 表达式这里在代码中有逻辑错误，
     * 并且程序抛出了异常以及程序在操作的过程中中止，将会发生什么。 通常情况下，一个全局的异常处理者会捕获这个异常，
     * 将异常打印成日记并报告给开发者，但是反之该程序将会继续执行其它操作。但是这里我们的 somethingUsefulOneAsync 仍然在后台执行，
     * 尽管如此，启动它的那次操作也会被终止。这个程序将不会进行结构化并发??????
     */
    // 注意，在这个示例中我们在 `main` 函数的右边没有加上 `runBlocking`
    @Test
    fun main4() {
        val time = measureTimeMillis {
            // 我们可以在协程外面启动异步执行
            val one = somethingUsefulOneAsync()
            val two = somethingUsefulTwoAsync()
            // 但是等待结果必须调用其它的挂起或者阻塞
            // 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
            runBlocking {
                println("The answer is ${one.await() + two.await()}")
            }
        }
        println("Completed in $time ms")
    }

    fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    /**
     * 结构化并发
     */
    @Test
    fun main5() = runBlocking<Unit> {
        val time = measureTimeMillis {
            suspend fun concurrentSum(): Int = coroutineScope {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                one.await() + two.await()
            }
            println("The answer is ${concurrentSum()}")
        }
        println("Completed in $time ms")
    }

    /**
     * 当任意一个子协程失败的时候，第一个 async 以及等待中的父协程都会被取消
     * 1.不用coroutineScope{}包裹时try-catch不起作用????
     */
    @Test
    fun main6() = runBlocking<Unit> {
        try {
            coroutineScope {
                val one = async<Int> {
                    try {
                        delay(Long.MAX_VALUE) // 模拟一个长时间的运算
                        42
                    } finally {
                        println("First child was cancelled")
                    }
                }
                val two = async<Int> {
                    println("Second child throws an exception")
                    throw ArithmeticException()
                }
                one.await() + two.await()
            }
        } catch (e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }

}
