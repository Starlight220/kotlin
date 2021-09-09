/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.generators.builtins.progressions

import org.jetbrains.kotlin.generators.builtins.ProgressionKind
import org.jetbrains.kotlin.generators.builtins.ProgressionKind.*
import org.jetbrains.kotlin.generators.builtins.areEqualNumbers
import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.BuiltInsSourceGenerator
import org.jetbrains.kotlin.generators.builtins.hashLong
import org.jetbrains.kotlin.generators.builtins.progressionIncrementType
import java.io.PrintWriter

class GenerateProgressions(out: PrintWriter) : BuiltInsSourceGenerator(out) {

    override fun getPackage() = "kotlin.ranges"
    override fun getFileAnnotations(): List<String> =
        // TODO: make source retention annotations ignored by LoadBuiltinsTest and move closer to usage
        listOf("""Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") // preserve parameter name of 'contains' override""")

    private fun generateDiscreteBody(kind: ProgressionKind) {
        val t = kind.capitalized
        val progression = "${t}Progression"

        val incrementType = progressionIncrementType(kind)
        fun compare(v: String) = areEqualNumbers(v)

        val zero = when (kind) {
            LONG -> "0L"
            else -> "0"
        }
        val checkZero = """if (step == $zero) throw kotlin.IllegalArgumentException("Step must be non-zero.")"""

        val stepMinValue = "$incrementType.MIN_VALUE"
        val checkMin =
            """if (step == $stepMinValue) throw kotlin.IllegalArgumentException("Step must be greater than $stepMinValue to avoid overflow on negation.")"""

        val hashCode = "=\n" + when (kind) {
            CHAR ->
                "        if (isEmpty()) -1 else (31 * (31 * first.code + last.code) + step)"
            INT ->
                "        if (isEmpty()) -1 else (31 * (31 * first + last) + step)"
            LONG ->
                "        if (isEmpty()) -1 else (31 * (31 * ${hashLong("first")} + ${hashLong("last")}) + ${hashLong("step")}).toInt()"
        }
        val elementToIncrement = when (kind) {
            CHAR -> ".code"
            else -> ""
        }
        val incrementToElement = when (kind) {
            CHAR -> ".toChar()"
            else -> ""
        }
        val one = if (kind == LONG) "1L" else "1"
        val sizeBody = "" +
                when (kind) {
                    CHAR -> "if (isEmpty()) 0 else (last - first) / step + $one"
                    else -> """
        when {
            isEmpty() -> 0
            step > 0 -> unsignedIncrementAndClamp(progressionUnsignedDivide(last - first, step))
            step < 0 -> unsignedIncrementAndClamp(progressionUnsignedDivide(first - last, -step))
            else -> error("Progression invariant is broken: step == 0")
        }""".trim()
                }

        out.println(
            """/**
 * A progression of values of type `$t`.
 */
public open class $progression
    internal constructor
    (
            start: $t,
            endInclusive: $t,
            step: $incrementType
    ) : Collection<$t>, kotlin.internal.ProgressionCollection {
    init {
        $checkZero
        $checkMin
    }

    /**
     * The first element in the progression.
     */
    public val first: $t = start

    /**
     * The last element in the progression.
     */
    public val last: $t = getProgressionLastElement(start$elementToIncrement, endInclusive$elementToIncrement, step)$incrementToElement

    /**
     * The step of the progression.
     */
    public val step: $incrementType = step

    override fun iterator(): ${t}Iterator = ${t}ProgressionIterator(first, last, step)

    /**
     * Checks if the progression is empty.
     *
     * Progression with a positive step is empty if its first element is greater than the last element.
     * Progression with a negative step is empty if its first element is less than the last element.
     */
    public override fun isEmpty(): Boolean = if (step > 0) first > last else first < last

    override fun equals(other: Any?): Boolean =
        other is $progression && (isEmpty() && other.isEmpty() ||
        ${compare("first")} && ${compare("last")} && ${compare("step")})

    override fun hashCode(): Int $hashCode

    override fun toString(): String = ${"if (step > 0) \"\$first..\$last step \$step\" else \"\$first downTo \$last step \${-step}\""}

    @SinceKotlin("1.6")
    override val size: Int
        get() = $sizeBody

    @SinceKotlin("1.6")
    override fun contains(value: $t): Boolean = when {
        @Suppress("USELESS_CAST") (value as Any? !is $t) -> false // TODO: Eliminate this check after KT-30016 gets fixed.
        step > $zero && value >= first && value <= last ||
        step < $zero && value <= first && value >= last -> value$elementToIncrement.mod(step) == first$elementToIncrement.mod(step)
        else -> false
    }

    @SinceKotlin("1.6")
    override fun containsAll(elements: Collection<$t>): Boolean =
        if (this.isEmpty()) elements.isEmpty() else (elements as Collection<*>).all { it in (this as Collection<Any?>) }

    companion object {
        /**
         * Creates $progression within the specified bounds of a closed range.
         *
         * The progression starts with the [rangeStart] value and goes toward the [rangeEnd] value not excluding it, with the specified [step].
         * In order to go backwards the [step] must be negative.
         *
         * [step] must be greater than `$stepMinValue` and not equal to zero.
         */
        public fun fromClosedRange(rangeStart: $t, rangeEnd: $t, step: $incrementType): $progression = $progression(rangeStart, rangeEnd, step)
    }
}"""
        )
        out.println()

    }

    override fun generateBody() {
        out.println("import kotlin.internal.getProgressionLastElement")
        out.println("import kotlin.internal.unsignedIncrementAndClamp")
        out.println("import kotlin.internal.progressionUnsignedDivide")
        out.println()
        for (kind in ProgressionKind.values()) {
            generateDiscreteBody(kind)
        }
    }
}
