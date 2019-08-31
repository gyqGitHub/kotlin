package com.example.pc.kotlin.base


interface MyInterface {
    val prop: Int
        //提供了访问器，但没有幕后字段
        get() = 1


    var a: String//抽象属性，必须实现

    fun bar()//抽象方法，必须实现

    fun foo() {//普通方法
        println("foo()>>>>>>")
    }
}

class Child : MyInterface {
    override var a: String = "aa"

    override fun bar() {
        println("bar>>>>>>>")
    }

}

fun main(args: Array<String>) {
    var child = Child()
    println(child.a)
    println(child.prop)
    child.foo()
    child.bar()
}
