package com.example.pc.kotlin.base

/**
 * 扩展函数
 */
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}

class C {
    fun foo() {
        println("member")
    }
}

fun C.foo() {
    println("extension")
}

/**
 * 扩展属性
 */
val <T> List<T>.lastIndex
    // = 1 不能初始化
    get() = this.size - 1

/**
 * 扩展声明为成员
 * 在类E内部你可以为类D声明扩展,则这个扩展只能在类E内使用
 * 类D:扩展接收者，类E：分发接收者
 */
class D {
    fun bar() {
        println("D---bar()")
    }

    private fun h() {
        print(1)
    }
}

class E {
    fun baz() {
        print("E---baz()")
    }

    fun D.foo() {
        bar()   // 调用 D.bar
        baz()   // 调用 C.baz
        toString()//扩展时调用同名方法，扩展接收者优先
        this@E.toString()
//        h()//不能访问扩展接收者的private方法
    }

    fun caller(d: D) {
        d.foo()   // 调用扩展函数
    }
}

open class F {}

class F1 : F() {}

/**
 * —— 分发接收者虚拟解析
 * —— 扩展接收者静态解析
 */
open class G {
    open fun F.foo() {
        println("F.foo in G")
    }

    open fun F1.foo() {
        println("F1.foo in G")
    }

    fun caller(f: F) {
        f.foo()   // 调用扩展函数
    }
}

class G1 : G() {
    override fun F.foo() {
        println("F.foo in G1")
    }

    override fun F1.foo() {
        println("F1.foo in G1")
    }
}

fun main() {
    G().caller(F())   // 输出 "F.foo in G"
    G1().caller(F())  // 输出 "F.foo in G1" —— 分发接收者虚拟解析
    G().caller(F1())  // 输出 "F.foo in G" —— 扩展接收者静态解析
}

fun main(args: Array<String>) {
    val list: MutableList<String> = mutableListOf("1", "2", "3")
    list.swap(0, 2)
    println(list.toString())
    C().foo()
    println(listOf(1, 2, 3).lastIndex)
}


