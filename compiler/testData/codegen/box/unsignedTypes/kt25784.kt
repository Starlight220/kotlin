// DONT_TARGET_EXACT_BACKEND: WASM
// WASM_MUTE_REASON: BINDING_RECEIVERS
// KJS_WITH_FULL_RUNTIME
// WITH_RUNTIME

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

class ByteDelegate(
    private val position: Int,
    private val uIntValue: KProperty0<UInt>
) {
    operator fun getValue(any: Any?, property: KProperty<*>): UByte {
        val uInt = uIntValue.get() shr (position * 8) and 0xffu
        return uInt.toUByte()
    }
}

class ByteDelegateTest {
    val uInt = 0xA1B2C3u
    val uByte by ByteDelegate(0, this::uInt)

    fun test() {
        val actual = uByte
        if (0xC3u.toUByte() != actual) throw AssertionError()
    }
}

fun box(): String {
    ByteDelegateTest().test()

    return "OK"
}