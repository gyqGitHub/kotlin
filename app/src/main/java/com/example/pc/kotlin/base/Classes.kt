package com.example.pc.kotlin.base

import properties.name

fun main(args: Array<String>) {

    println(Empty())
    println(Person("gyq"))
}

/**
 * 1.关键字class
 * 2.类名 Person
 * 3.类头：主构造函数，构造参数(可省略，主构造函数：没有代码块，初始化代码在init初始化块,构造参数：默认是val,如果添加了var/val修饰，则认为该参数是该类的属性)
 * 4.类体：花括号包围(可省略)
 */
open class Person constructor(name: String = "") {
    var age: Int = 1
    lateinit var name: String

    //主构造函数参数可以在初始化块可见
    init {
        this.name = name
        println("初始化代码块--姓名$name")
    }

    init {
        println("初始化代码块--年龄：$age")
    }


    fun eat() {
        println("$name eat")
    }
}

/**
 * 次构造函数需要先调用主构造函数
 */
class Animal(var name:String){
//    constructor(){
//
//    }
}

class Empty
