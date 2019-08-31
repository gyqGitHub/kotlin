package visibilities


external fun bar()//目测external是在非同一个module中不可见

open class Outer {
    private val a = 1
    protected open val b = 2
    internal val c = 3
    val d = 4  // 默认 public

    protected class Nested {
        public val e: Int = 5
    }
}

class Subclass : Outer() {
    // a 不可见
    // b、c、d 可见
    // Nested 和 e 可见
    init {
    }

    override val b = 5   // “b”为 protected,且不能声明为private，可以生成public
}

class Unrelated(o: Outer) {
    // o.a、o.b 不可见
    // o.c 和 o.d 可见（相同模块）
    // Outer.Nested 不可见，Nested::e 也不可见
}

/**
 * 密封类不能同一个文件之外继承
 */
/*class Cosnt() : Expr(){

}*/

fun main(args: Array<String>) {

}
