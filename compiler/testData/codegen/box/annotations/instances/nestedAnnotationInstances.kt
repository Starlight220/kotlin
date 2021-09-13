// IGNORE_BACKEND_FIR: JVM_IR
// TARGET_BACKEND: JVM_IR
// TARGET_BACKEND: NATIVE

// WITH_RUNTIME
// !LANGUAGE: +InstantiationOfAnnotationClasses

annotation class NestedAnnotation(val value: String)
annotation class OuterAnnotation(val nested: NestedAnnotation)
class Outer(val nested: NestedAnnotation, val outer: OuterAnnotation)

fun box(): String {
    val anno = Outer(NestedAnnotation("O"), OuterAnnotation(NestedAnnotation("K")))
    return anno.nested.value + anno.outer.nested.value
}
