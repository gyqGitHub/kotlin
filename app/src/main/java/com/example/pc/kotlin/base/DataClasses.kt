package com.example.pc.kotlin.base

import visibilities.bar

/**
 * 1.必须要有主构造函数
 * 2.主构造函数必须要有一个参数，且必须用val/var声明
 * 3.数据类不能是抽象，密封，开放，或者内部的
 * 4.在 JVM 中，如果生成的类需要含有一个无参的构造函数，则所有的属性必须指定默认值
 * 自动根据主构造函数参数生成toString(),equals(),hashCode()，copy（）方法
 */
data class User(var name: String) {
    var age: Int = 0

    override fun equals(other: Any?): Boolean {
        println("自定义了toString()")
        return super.equals(other)
    }

}

fun main(args: Array<String>) {
    var user = User("gyq")
    var user2 = User("gyq")
    var copyUser = user.copy(name = "lmq")
    print(copyUser)
    println(user.equals(user2))
    val (name) = copyUser
    println("$name, ${copyUser.age} years of age") // 输出 "Jane, 35 years of age"

    bar()
}