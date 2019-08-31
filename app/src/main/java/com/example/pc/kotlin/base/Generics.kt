package com.example.pc.kotlin.base

/**
 * 泛型函数
 * 泛型放在函数名前
 * 泛型类
 * 泛型放在类名后
 */
fun <T> testGenerics(t: T) {

}

class Box<T>(t: T) {
    var value = t
}

interface Source<out T> {
    fun nextT(): T
}

/**
 * where关键字
 */
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
        where T : CharSequence,
              T : Comparable<T> {
    return list.filter { it > threshold }.map { it.toString() }
}

/**
 * 声明出形变(参数类型声明的时候提供)：协变和逆变
 * 可用派生类替换则为协变
 * 可用基类替换则为逆变
 */
fun demo(strs: Source<String>): Source<Any> {
    val objects: Source<Any> = strs // 这个没问题，因为 T 是一个 out-参数,可以赋值给T的父类
    val nextT = objects.nextT()
    return strs
}

/**
 * 跟Java一样会有泛型擦除
 */

fun main(args: Array<String>) {
    val box = Box(1)
    val box2: Box<String> = Box("")
}

