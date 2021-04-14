@file:Suppress("UNCHECKED_CAST")

package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

operator fun <T> Spek<T, Number>.plus(
    other: Spek<T, out Number>
) = BinaryOperationSpek<T, Number, Number>(
    this,
    other as Spek<T, Number>
) { a, b -> a().toDouble() + b().toDouble() }

operator fun <T> Spek<T, Number>.plus(
    other: Number
) = BinaryOperationSpek<T, Number, Number>(
    this, ValueSpek(other)
) { a, b -> a().toDouble() + b().toDouble() }

operator fun <T> Spek<T, Number>.minus(
    other: Spek<T, out Number>
) = BinaryOperationSpek<T, Number, Number>(
    this,
    other as Spek<T, Number>
) { a, b -> a().toDouble() - b().toDouble() }

operator fun <T> Spek<T, Number>.minus(
    other: Number
) = BinaryOperationSpek<T, Number, Number>(
    this, ValueSpek(other)
) { a, b -> a().toDouble() - b().toDouble() }

class GreaterThanSpek(
    private val base: Spek<Number, Number>,
    private val allowEquals: Boolean = false
) : Spek<Number, Boolean> {
    constructor(base: Number, allowEquals: Boolean = false) : this(ValueSpek(base), allowEquals)

    override val children = listOf(base)

    override fun evaluate(visitor: SpekVisitor, candidate: Number): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                candidate.toDouble() >= base.evaluate(visitor, candidate).toDouble()
            } else {
                candidate.toDouble() > base.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class LessThanSpek(
    private val base: Spek<Number, Number>,
    private val allowEquals: Boolean = false
) : Spek<Number, Boolean> {
    constructor(base: Number, allowEquals: Boolean = false) : this(ValueSpek(base), allowEquals)

    override val children = listOf(base)

    override fun evaluate(visitor: SpekVisitor, candidate: Number): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                candidate.toDouble() <= base.evaluate(visitor, candidate).toDouble()
            } else {
                candidate.toDouble() < base.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class LargestSpek<T>(
    private vararg val bases: Spek<T, out Number>
) : Spek<T, Number> {

    override val children = bases.toList()

    override fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        var largest = bases.asList().first().evaluate(visitor, candidate)

        for (base in bases.asList().drop(1)) {
            val tempValue = base.evaluate(visitor, candidate)
            if (tempValue.toDouble() > largest.toDouble()) {
                largest = tempValue
            }
        }

        return visiting(visitor) { largest }
    }
}

class SmallestSpek<T>(
    private vararg val bases: Spek<T, Number>
) : Spek<T, Number> {

    override val children = bases.toList()

    override fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        var smallest = bases.asList().first().evaluate(visitor, candidate)

        for (base in bases.asList().drop(1)) {
            val tempValue = base.evaluate(visitor, candidate)
            if (tempValue.toDouble() < smallest.toDouble()) {
                smallest = tempValue
            }
        }

        return visiting(visitor) { smallest }
    }
}
