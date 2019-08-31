package com.example.pc.kotlin.base

import java.io.File

/**
 * 有助于缩短较长的泛型类型
 */
typealias FileSet = Set<File>

typealias FileTable<K> = MutableMap<K, MutableList<File>>

/**
 * 为函数类型提供另外的别名
 * 类型别名给函数类型起一个别称
 */
typealias MyHandler = (Int, String, Any) -> Unit

typealias Predicate<T> = (T) -> Boolean

fun foo(p: Predicate<Int>) = p(42)

fun main(args: Array<String>) {
    var fileSet: FileSet = setOf()
    //lambda作为一个类型
    val f: (Int) -> Boolean = { it > 0 }
    println(foo(f)) // 输出 "true"

    val p: Predicate<Int> = { it > 0 }
    println(listOf(1, -2).filter(p)) // 输出 "[1]"
}