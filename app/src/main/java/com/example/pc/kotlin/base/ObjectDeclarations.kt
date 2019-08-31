package com.example.pc.kotlin.base

interface B {
    fun test()
}

open class A(x: Int) {
    open val y: Int = x

}

/**
 * 单利模式
 */
object Singleton {
    fun test() {

    }
}

/**
 * 伴生对象
 * 请注意，即使伴生对象的成员看起来像其他语言的静态成员，在运行时他们仍然是真实对象的实例成员
 * 在 JVM 平台，如果使用 @JvmStatic 注解，你可以将伴生对象的成员生成为真正的静态方法和字段 还可以实现接口：
 *
 */
class Gyq9 {
    companion object : B {
        override fun test() {
            g()
        }

        private fun g() {

        }
    }
}

fun main(args: Array<String>) {

    /**
     * 对象表达式
     * 创建一个继承自某个（或某些,或不继承）类型的匿名类的对象
     */
    var b: B = object : B {
        override fun test() {

        }

    }

    val ab: A = object : A(1), B {
        override fun test() {

        }

        override val y = 15
    }

    val ab0 = object {
        var age: Int = 9
    }

    println(ab0.age)

    Singleton.test()

    Gyq9.test()
    Gyq9.Companion.test()
    println(Gyq9.equals(Gyq9.Companion))
}
