package com.example.pc.kotlin.base;

/**
 * ${description}
 *
 * @author gyq
 * @date 2019/8/15
 */
public class Generics {
    interface Source<T> {
        T nextT();
    }

    // Java
    void demo(Source<String> strs) {
//        Source<Object> objects = strs; // ！！！在 Java 中不允许
        // ……
    }
}
