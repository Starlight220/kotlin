// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: BINDING_RECEIVERS
class A {
    var value: String = "fail1"
        private set

    inner class B {
        fun foo(): kotlin.reflect.KMutableProperty0<String> = this@A::value
    }
}

class C {
    var value: String = "fail2"
        private set

    fun bar(): kotlin.reflect.KMutableProperty0<String> {
        class D {
            fun foo(): kotlin.reflect.KMutableProperty0<String> = this@C::value
        }

        return D().foo()
    }
}

fun box(): String {
    val a = A()
    a.B().foo().set("O")

    val c = C()
    c.bar().set("K")

    return a.value + c.value
}