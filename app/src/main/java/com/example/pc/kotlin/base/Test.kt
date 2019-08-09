package test

fun add1(a: Int, b: Int) {
    print(a + b)
}

/**
 * 命名参数
 */
fun add(a: Int = 1, b: Int = 9) {
    print("$a  +  $b = " + (a + b))
}

/**
 * 可变长参数
 */
fun testVararg(vararg v: String) {
    for (s in v) {
        print(s)
    }
}

fun main(args : Array<String>){
    add(a =9)
}