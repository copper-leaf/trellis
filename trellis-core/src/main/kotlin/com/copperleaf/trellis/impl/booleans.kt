package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

// Extension Methods
//----------------------------------------------------------------------------------------------------------------------

infix fun <T> Spek<T, Boolean>.and(other: Spek<T, Boolean>) = BinaryOperationSpek(this, other) { a, b -> a() && b() }
infix fun <T> Spek<T, Boolean>.andNot(other: Spek<T, Boolean>) = BinaryOperationSpek(this, other) { a, b -> a() && !b() }
infix fun <T> Spek<T, Boolean>.or(other: Spek<T, Boolean>) = BinaryOperationSpek(this, other) { a, b -> a() || b() }
infix fun <T> Spek<T, Boolean>.orNot(other: Spek<T, Boolean>) = BinaryOperationSpek(this, other) { a, b -> a() || !b() }

fun <T> Spek<T, Boolean>.not() = UnaryOperationSpek(this) { a -> !a() }

operator fun <T> Spek<T, Boolean>.plus(other: Spek<T, Boolean>) = this.and(other)
operator fun <T> Spek<T, Boolean>.unaryMinus() = this.not()

/**
 * Check that two Speks have equal values. Number values are converted to doubles before checking equality.
 */
class EqualsSpek<T>(private val base: Spek<T, T>) : Spek<T, Boolean> {
    constructor(base: T) : this(ValueSpek(base))

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Boolean {
        return visiting(visitor) {
            val a = candidate
            val b = base.evaluate(visitor, candidate)

            if (a is Number && b is Number) {
                a.toDouble() == b.toDouble()
            } else {
                a == b
            }
        }
    }
}
