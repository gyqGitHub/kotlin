package properties

/**
 *属性定义关键字：val(value) 只读 var(variable)读写
 * var <propertyName>[: <PropertyType>] [= <property_initializer>]
 * [<getter>]
 * [<setter>]
 * 属性类型如果可以从初始器 （或者从其 getter 返回值，如下文所示）中推断出来，也可以省略
 */
var className = "Properties"
var day
    get() = "2019-08-08"
    set(value) {
        println("自定义setter$value")
    }//去掉set会报错 自定义的 setter，那么每次给属性赋值时都会调用它,但是day的值没变？？？ --->幕后字段---如果自定义了两个访问器方法且没有用到filed字段，其实只是定义了两个方法而已

/**
 * 将会为该属性生成一个幕后字段：
 * 1.如果属性至少一个访问器使用默认实现，一定有幕后字段？？？怎么证明
 * 2.或者自定义访问器通过 field 引用幕后字段
 */
var time = "13:34"
    get() {
        return "幕后字段：$field"
    }
    set(value) {
        if (value.contains("12")) field else field = "default"
    }

/**
 * 编译期常量
 * 1.位于顶层或者是 object 声明 或 companion object 的一个成员
 * 2.以 String 或原生类型值初始化
 * 3.没有自定义 getter
 */
const val PI = 3.14
//    get() = ""
//const val HUMAN = Person()

/**
 * 延迟初始化属性与变量
 * 1.延迟初始化一定要带类型
 */
lateinit var name: String

fun main(args: Array<String>) {
    //局部变量没有getter/setter
    var className = "another"

    println(className)

    name = "lateInit"
    println(name)

    day = "2020-02-02"
    println(day)

    time = "13:13"
    println(time)

//    Person().address = ""
//    Person().postal = ""
}

class Person {
    //_address 是 address的幕后属性，对外只读，对内可修改，维持多了一个变量，网上还看到一种方法：设置address的为var，setter设置为私有,如postal属性
    private var _address = "深圳"
    val address: String
        get() {
            return _address
        }

    var postal: String = ""
        private set(value) {
            field = value
        }

}


