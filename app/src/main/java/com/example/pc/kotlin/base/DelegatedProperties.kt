package com.example.pc.kotlin.base

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Example {
    var p: String by Delegate()
}

class Delegate {
    /*override fun getValue(thisRef: String, property: KProperty<*>): String {
        return ""
    }

    override fun setValue(thisRef: String, property: KProperty<*>, value: String) {

    }*/

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }

}

val lazyValue: String by lazy(LazyThreadSafetyMode.NONE) {
    println("lazy")
    "Hello"
}

class UserA {
    var name: String by Delegates.vetoable("initialValue") { property, oldValue, newValue ->
        println("$property's oldValue = $oldValue,newValue = $newValue ")
        newValue.equals("gyq")
    }
}

class UserB(val map: Map<String, Any>) {
    val name: String by map
}

fun main(args: Array<String>) {
    println(Example().p)
    Example().p = "New Name"

    println(lazyValue)
    println(lazyValue)

    val user = UserA()
    user.name = "gyq1"
    println(user.name)

    println(UserB(mapOf("name" to "gyq")).name)

}
