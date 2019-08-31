package com.example.pc.kotlin.base

/**
 * 函数类型
 * 1.都有一个圆括号括起来的参数类型列表以及一个返回类型(A, B) -> C
 * 特殊() -> Unit
 * 2.函数类型可以有一个额外的接收者类型
 * 类型 A.(B) -> C 表示可以在 A 的接收者对象上以一个 B 类型参数来调用并返回一个 C 类型值的函数（怎么调用呢？？）
 * 3.挂起函数属于特殊种类的函数类型
 * 例如 suspend () -> Unit 或者 suspend A.(B) -> C
 *
 * 注意：
 * 1.函数类型表示法可以选择性地包含函数的参数名：(x: Int, y: Int) -> Point。 这些名称可用于表明参数的含义
 * 2.如需将函数类型指定为可空，请使用圆括号：((Int, Int) -> Int)?
 * 3.(Int) -> ((Int) -> Unit)
 * 4.箭头表示法是右结合的，(Int) -> (Int) -> Unit 与前述示例等价，但不等于 ((Int) -> (Int)) -> Unit
 */


/**
 * 函数类型实例化
 * 1.使用函数字面值的代码块
 * lambda 表达式: { a, b -> a + b }
 * 匿名函数: fun(s: String): Int { return s.toIntOrNull() ?: 0 }
 * 带有接收者的函数字面值????可用作带有接收者的函数类型的值。
 *
 * 2.使用已有声明的可调用引用
 * 顶层、局部、成员、扩展函数：::isOdd、 String::toInt
 * 顶层、成员、扩展属性：List<Int>::size
 * 造函数：::Regex
 * 这包括指向特定实例成员的绑定的可调用引用：foo::toString。?????
 *
 * 3.使用实现函数类型接口的自定义类的实例
 * class IntTransformer: (Int) -> Int {
override operator fun invoke(x: Int): Int = TODO()
}

val intFunction: (Int) -> Int = IntTransformer()
 */
fun main(args: Array<String>) {

}
