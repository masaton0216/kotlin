// !LANGUAGE: +InlineClasses
// IGNORE_BACKEND: JVM_IR, JS_IR, JS

inline class Z(val value: Int)

fun eq(a: Z?, b: Z) = a == b

fun box(): String {
    if (!eq(Z(1), Z(1))) throw AssertionError()
//    if (eq(Z(1), Z(2))) throw AssertionError()
//    if (eq(null, Z(0))) throw AssertionError()

    return "OK"
}