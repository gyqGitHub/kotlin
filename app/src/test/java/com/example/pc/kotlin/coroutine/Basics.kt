package com.example.pc.kotlin.coroutine

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.coroutines.CoroutineContext

/**
 * 协程基础
 * 1.协程作用域，协程上下文，协程上下文元素(Job)
 */
class Basics {
    /**
     * 本质上，协程是轻量级的线程
     * 协程的创建方式：
     * 1.协程作用域+协程构建器一起创建，GlobalScope.launch{},GlobalScope是协程作用域，launch{}是协程构建器
     * --1.协程作用域：属性+方法[CoroutineScope]
     * ------1.属性：协程上下文[CoroutineContext]
     * ------2.方法：只有一系列的扩展方法(协程构建器)
     * ------3.获取独立作用域实例的最好方法是[CoroutineScope()] and [MainScope()]工厂方法???
     *
     * --2.协程构建器：
     * ------1.每个协程构建器都是协程作用域的扩展[CoroutineScope.launch]
     * ------2.构建器扩展方法：[CoroutineScope.launch],[CoroutineScope.async]
     *
     * --3.协程作用域派生类[GlobalScope]
     *
     * 2.[runBlocking]创建一个会阻塞当前线程的协程
     */
    @Test
    fun main1() {
        GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("World!") // 在延迟后打印输出
        }
        println("Hello,") // 协程已在等待时主线程还在继续
        Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
    }

    /**
     * [runBlocking]创建一个会阻塞当前线程的协程
     * 使用范围：不应在协程内中使用，一般用于main函数和测试代码中
     */
    @Test
    fun main2() {
        GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L)
            println("World!")
        }
        println("Hello,") // 主线程中的代码会立即执行
        runBlocking {
            // 但是这个表达式阻塞了主线程
            delay(2000L)  // ……我们延迟 2 秒来保证 JVM 的存活
        }
    }

    /**
     * 显式（以非阻塞方式）等待所启动的后台 Job 执行结束
     * 1.Job概念:
     * ---0.协程上下文元素，一个Job实例代表协程本身
     * ---1.可取消且拥有生命周期
     * ---2.有父子之分，父Job取消会立即导致所有子Job的取消；而子Job如果抛出CancellationException之外的异常，会导致父Job取消，因而导致其他子Job的取消
     *      //适用于[launch][CoroutineScope.launch]，而如果是通过[async][CoroutineScope.async]或其他会返回结果的协程构建器，不会有未捕获异常，异常都被封装在了返回的结果中
     *      //这种行为是可以通过[SuperVisorJob]自定义的
     *      父Job可以通过调用[Job.cancelChildren]取消所有子Job而不至于取消了自己。
     * ---3.Job的执行不会有结果返回，若想有返回值，参考[Deferred]
     * ---4.父Job会等待子Job的执行
     *
     * 2.创建方式：
     * ---通过构建器[CoroutineScope.launch]或者工厂方法Job()
     *
     * 3.状态：
     * * A job has the following states:
     * | **State**                        | [isActive] | [isCompleted] | [isCancelled] |
     * | -------------------------------- | ---------- | ------------- | ------------- |
     * | _New_ (optional initial state)   | `false`    | `false`       | `false`       |
     * | _Active_ (default initial state) | `true`     | `false`       | `false`       |
     * | _Completing_ (transient state)   | `true`     | `false`       | `false`       |
     * | _Cancelling_ (transient state)   | `false`    | `false`       | `true`        |
     * | _Cancelled_ (final state)        | `false`    | `true`        | `true`        |
     * | _Completed_ (final state)        | `false`    | `true`        | `false`       |
     * Job创建后一般处于_Active_状态；但是通过GlobalScope.launch(start = CoroutineStart.LAZY)创建时会处于_New_状态，通过调用[start] or [join]可进入到_Active_状态
     * 如果Job抛出了异常或者调用了[cancel]方法，立即进入_Cancelling_状态，最终变成_Cancelled_
     *                                       wait children
     * +-----+ start  +--------+ complete   +-------------+  finish  +-----------+
     * | New | -----> | Active | ---------> | Completing  | -------> | Completed |
     * +-----+        +--------+            +-------------+          +-----------+
     *                  |  cancel / fail       |
     *                  |     +----------------+
     *                  |     |
     *                  V     V
     *              +------------+                           finish  +-----------+
     *              | Cancelling | --------------------------------> | Cancelled |
     *              +------------+                                   +-----------+
     * 注意，_completing_是纯粹的内部状态，对于外部观察者来说，是处于Active状态
     * 注意，_Cancelled_,_Completed_时[isCompleted]都为true
     *
     * 4.取消异常的抛出
     * ---1.调用[cancel]
     * ---2.发生异常
     *
     * 5.Job及其派生的接口方法都是线程安全的
     *   常用方法有：
     * ---1.start()
     * ---2.join()
     * ---3.cancel()
     * ---4.cancelAndJoin()
     * ---5.cancelChildren()
     *
     *
     */
    @Test
    fun main3() = runBlocking {
        val job = GlobalScope.launch {
            // 启动一个新协程并保持对这个作业的引用
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        job.join() // 等待直到子协程执行结束
        println(job)
    }

    @Test
    fun main4() = runBlocking {
        //父Job取消会立即导致所有子Job的取消
        val parentJob1 = launch {
            //
            val childJob = launch {
                repeat(100) {
                    delay(100)
                    println("打印次数——$it")
                }
            }
        }
        delay(550)
        parentJob1.cancelAndJoin() // 等待直到子协程执行结束
        println("父Job取消会立即导致所有子Job的取消")

        val parentJob2 = GlobalScope.launch {
            //
            val childJob = launch {
                repeat(100) {
                    delay(100)
                    println("---childJob打印次数——$it")
                    if (it == 3) {
                        throw ArithmeticException()
//                        throw CancellationException()
                    }
                }
            }
            val otherChildJob = launch {
                repeat(100) {
                    delay(100)
                    println("---otherChildJob其他子协程打印次数——$it")
                }
            }

            repeat(100) {
                delay(200)
                println("---parentJob2父协程打印次数——$it")
            }
        }
        delay(500)
        parentJob2.join()
        println("子Job如果抛出CancellationException之外的异常，会导致父Job取消，因而导致其他子Job的取消")
    }

    @Test
    fun main5() = runBlocking {
        val job = GlobalScope.launch {
            var job1:Job? = null
            job1 = launch(start = CoroutineStart.LAZY) {
                delay(1000)
            }

            println("job1.isActive = ${job1?.isActive}")
            val job2 = launch {
                delay(1000)
            }
            println("job.isActive = $isActive")
            println("job2.isActive = ${job2.isActive}")
            job1?.start()
            println("job1.isActive = ${job1?.isActive}")
            try {
                job1?.cancel()
            } catch (e: CancellationException) {
                println("job1取消了")//没有调用呀？？？
            }
            delay(2000)
            println("job1.isCompleted = ${job1?.isCompleted}")
            println("job1.isCancelled = ${job1?.isCancelled}")
            println("job2.isCompleted = ${job2.isCompleted}")
            println("job2.isCancelled = ${job2.isCancelled}")
        }
        job.join() // 等待直到子协程执行结束
    }
}
