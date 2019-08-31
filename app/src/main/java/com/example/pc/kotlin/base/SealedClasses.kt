package sealedclasses

/**
 * 密封类是抽象的
 */
sealed class Expr {
    abstract var value: Int
}

class Const(override var value: Int) : Expr()

data class Sum(var age: Int, override var value: Int) : Expr()

object Not : Expr() {
    override var value: Int
        get() = 1
        set(value) {}

}

fun eval(expr: Expr): Int {
    return when (expr) {
        is Const -> expr.value
        is Sum -> expr.age
        is Not -> 99
    }
}

fun main(args: Array<String>) {
    print(eval(Const(99)))
}
