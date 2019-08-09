/**
 * 定义包：
 * 1.包的声明应处于源文件顶部
 * 2.源代码可以在文件系统的任意位置(目录与包的结构无需匹配)
 */
package syntax

import properties.PI

fun getPerimeter(radius: Int) = radius * PI * 2

fun parseInt(arg: String): Int? {
    return arg.toIntOrNull()
}

fun typeCast(obj: Any) {
    if (obj is String) {
        println(obj.length)
    } else {
        println(obj.toString())
    }

}

fun testFor() {
    for (item in 1..10) {
        println(item in 8..10)
    }
}

fun testWhen(obj: Any) {
    println(when (obj) {
        1 -> "1"
        is String -> "String"
        else -> "else"
    })


}

/**
 * 基本语法
 */
fun main(args: Array<String>) {
    println(getPerimeter(1))

    println(parseInt("ss") == null)

    typeCast("55555")

    testFor()

    testWhen("gyq")
}

