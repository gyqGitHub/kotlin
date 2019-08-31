package com.example.pc.kotlin.base

/**
 * 委托是继承的替代方案
 */

interface Base {
    var x: Int
    fun print()
}

class BaseImpl(override var x: Int) : Base {
    override fun print() {
        print(x)
    }
}

class Derived(b: Base) : Base by b {
    override var x = 88888888
}

fun main(args: Array<String>) {
    val b = BaseImpl(99)
    Derived(b).print()
}