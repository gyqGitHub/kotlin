package com.example.pc.kotlin.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main() {
//    dispatcherAndThread()
//    unConfined()
//    debugCoroutine()
//    changeDifferentThread()
//    childCoroutine()
//    parentCoroutine()
//    nameCoroutine()
    testCoroutineScope()
}

/**
 * 协程上下文与调度器
 * 协程上下文是各种不同元素的集合。其中主元素是协程中的 Job,job中有调度器
 */
fun dispatcherAndThread() {
    runBlocking {
        //thread main
        println("runBlocking111      : I'm working in thread ${Thread.currentThread().name}")

        //thread DefaultDispatcher-worker-2
        GlobalScope.launch {
            //调度器Dispatchers.Default
            println("GlobalScope.launch      : I'm working in thread ${Thread.currentThread().name}")
        }

        //thread main
        launch {
            // 运行在父协程的上下文中，即 runBlocking 主协程,为啥最后执行呢？？？优先子协程先执行吗？
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        }

        //thread main
        launch(Dispatchers.Unconfined) {
            // 不受限的——将工作在主线程中
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }

        //DefaultDispatcher-worker-2
        launch(Dispatchers.Default) {
            // 将会获取默认调度器 当协程在 GlobalScope 中启动的时候使用
            println("Default               : I'm working in thread ${Thread.currentThread().name}")
        }

        //thread MyOwnThread
        launch(newSingleThreadContext("MyOwnThread")) {
            // 将使它获得一个新的线程
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }
}

/**
 * 非受限调度器 vs 受限调度器
 * 当 delay 函数调用的时候，非受限的那个协程在默认的执行者线程种恢复执行
 */
fun unConfined() {
    runBlocking {
        launch(Dispatchers.Unconfined) {
            // 非受限的——将和主线程一起工作
            //该协程的上下文继承自 runBlocking {...} 协程并在 main 线程中运行，当 delay 函数调用的时候，非受限的那个协程在默认的执行者线程种恢复执行
            println("Unconfined      : I'm working in thread [${Thread.currentThread().name}]")
            delay(1500)
            println("Unconfined      : After delay in thread [${Thread.currentThread().name}]")
        }
        launch {
            // 父协程的上下文，主 runBlocking 协程
            println("main runBlocking: I'm working in thread [${Thread.currentThread().name}]")
            delay(1000)
            println("main runBlocking: After delay in thread [${Thread.currentThread().name}]")
        }
    }
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 调试协程和线程
 * ??? 使用 -Dkotlinx.coroutines.debug JVM 参数运行下面的代码 ???
 * [main @coroutine#2] I'm computing a piece of the answer
 * [main @coroutine#3] I'm computing another piece of the answer
 * [main @coroutine#1] The answer is 42
 *
 */
fun debugCoroutine() {
    runBlocking {
        val a = async {
            log("I'm computing a piece of the answer")
            6
        }
        val b = async {
            log("I'm computing another piece of the answer")
            7
        }
        log("The answer is ${a.await() * b.await()}")
    }

}

/**
 * 同一个协程在不同的线程中跳转
 * 使用 -Dkotlinx.coroutines.debug JVM 参数运行下面的代码
 * 注意：其中一个使用 runBlocking 来显式指定了一个上下文，并且另一个使用 withContext 函数来改变协程的上下文，而仍然驻留在相同的协程中
 * [Ctx1 @coroutine#1] Started in ctx1
 * [Ctx2 @coroutine#1] Working in ctx2
 * ----111-----true
 * My job is DispatchedCoroutine{Active}@388a8c08
 * ----222-----false
 * [Ctx1 @coroutine#1] Back to ctx1
 */
fun changeDifferentThread() {
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            runBlocking(ctx1) {
                println("My job is ${coroutineContext[Job]}")
                log("Started in ctx1")
                var job: Job? = null
                withContext(ctx2) {
                    log("Working in ctx2")
                    job = coroutineContext[Job]
                    println("----111-----" + job?.isActive)
                    println("My job is ${coroutineContext[Job]}")
                }
                println("----222-----" + job?.isActive)
                log("Back to ctx1")
            }
        }
    }
}

/**
 * 当一个协程被其它协程在 CoroutineScope 中启动的时候， 它将通过 CoroutineScope.coroutineContext 来承袭上下文，
 * 并且这个新协程的 Job 将会成为父协程作业的 子 作业。当一个父协程被取消的时候，所有它的子协程也会被递归的取消。
 * 然而，当使用 GlobalScope 来启动一个协程时，则新协程的作业没有父作业。 因此它与这个启动的作用域无关且独立运作
 */
fun childCoroutine() {
    runBlocking {
        // 启动一个协程来处理某种传入请求（request）
        val request = launch {
            // 孵化了两个子作业, 其中一个通过 GlobalScope 启动
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // 另一个则承袭了父协程的上下文
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // 取消请求（request）的执行
        delay(1000) // 延迟一秒钟来看看发生了什么
        println("main: Who has survived request cancellation?")
    }
}

/**
 * 一个父协程总是等待所有的子协程执行结束。父协程并不显式的跟踪所有子协程的启动，并且不必使用 Job.join 在最后的时候等待它们
 * 注意调用和不调用join()的区别，调用会造成阻塞的
 */
fun parentCoroutine() {
    runBlocking {
        // 启动一个协程来处理某种传入请求（request）
        val request = launch {
            repeat(3) { i ->
                // 启动少量的子作业
                launch {
                    delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒的时间
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // 等待请求的完成，包括其所有子协程
        println("Now processing of the request is complete")
    }
}

/**
 * 命名协程
 * [main @main#1] Started main coroutine
 * [main @v1coroutine#2] Computing v1
 * [main @v2coroutine#3] Computing v2
 * [main @main#1] The answer for v1 / v2 = 42
 */
fun nameCoroutine() {
    runBlocking {
        log("Started main coroutine")
        // 运行两个后台值计算
        val v1 = async(CoroutineName("v1coroutine")) {
            delay(500)
            log("Computing v1")
            252
        }
        val v2 = async(CoroutineName("v2coroutine")) {
            delay(1000)
            log("Computing v2")
            6
        }
        log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
    }
}

class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    fun destroy() {
        cancel() // Extension on CoroutineScope
    }
    // 继续运行……

    // class Activity continues
    fun doSomething() {
        // 在示例中启动了 10 个协程，且每个都工作了不同的时长
        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
                println("Coroutine $i is done")
            }
        }
    }
} // Activity 类结束

fun testCoroutineScope(){
    runBlocking {
        val activity = Activity()
        activity.doSomething() // 运行测试函数
        println("Launched coroutines")
        delay(500L) // 延迟半秒钟
        println("Destroying activity!")
        activity.destroy() // 取消所有的协程
        delay(1000) // 为了在视觉上确认它们没有工作
    }
}