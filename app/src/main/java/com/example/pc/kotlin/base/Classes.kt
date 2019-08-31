package com.example.pc.kotlin.base

import android.content.Context
import android.view.View

fun main(args: Array<String>) {

    println(Empty())
    Person("gyq").eat()
    var animal = Animal()
//    var foot = Animal.Foot()//内部类会报错，构造方法只能在Animal对象中使用
    var foot = animal.Foot()
    foot.run()
    var hand = Animal.Hand()
    hand.shake()
    Dog()
}

/**
 * 1.关键字class
 * 2.类名 Person
 * 3.类头：主构造函数，构造参数(可省略，主构造函数：没有代码块，初始化代码在init初始化块,构造参数：默认是val,如果添加了var/val修饰，则认为该参数是该类的属性)
 * 4.类体：花括号包围(可省略)
 */
open class Person constructor(var name: String = "") {
    var age: Int = 1
    lateinit var sex: String

    //主构造函数参数可以在初始化块可见
    init {
        this.name = name
        println("初始化代码块--姓名$name")
    }

    init {
        println("初始化代码块--年龄：$age")
    }


    fun eat() {
        this.sex = "man"
        println("$sex $name eat")
    }
}

/**
 * 次构造函数需要先调用主构造函数
 */
open class Animal(open var name: String) {

    init {
        println("基类先初始化")
    }

    constructor() : this("cat") {
        println("次构造函数可以不带任何参数")

    }

    open fun shit() {
        println("shit......")
    }

    inner class Foot {
        fun run() {
            println("${name} run......")
        }
    }

    class Hand {
        fun shake() {
            println("$this shake.......")
        }
    }
}

interface T{
    fun shit(){

    }
}

open class Dog(var color: String) : Animal(),T {
    override var name: String = "覆盖属性name"
        get() = ""

    init {
        println("派生类初始化")
    }


    constructor() : this("") {
        super<Animal>.shit()
    }

    final override fun shit() {
        super<T>.shit();
        println("重写的shit......")
    }

    inner class Foot {
        fun run() {
            println("${name} run......")
            super@Dog.equals("")
        }
    }
}

class MyView : View {
    constructor(ctx: Context) : super(ctx)
}

/**
 * 可以定义不带类体的类
 */
class Empty
