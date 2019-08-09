package base

fun test_() {
    val oneMillion = 1_000_00_0
    println(oneMillion)
}

fun testBox() {
    val a = 1000
    println(a === 1000)
    val b = 1000
    println(a === b)

    val c: Int? = 1000
    println(a === c)
    println(a == c)//相等性

    val d = 10
    val e: Int? = 10
    println(d === e)//同一性

}


fun testArray() {
    val array = arrayOf("a", "b", "c")
    println("b" in array)
    val arr = IntArray(7) { it * 2 }
    for ((i, v) in arr.withIndex()) {
        println("" + i + "==" + v)
    }
}

fun main(args: Array<String>) {
    test_()
    testBox()

    testArray()
}
