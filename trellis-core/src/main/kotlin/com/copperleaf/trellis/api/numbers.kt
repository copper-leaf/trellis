package com.copperleaf.trellis.api

operator fun <T> Spek<T, out Number>.plus(other: Spek<T, out Number>) = AddSpek(this, other)
operator fun <T> Spek<T, Number>.plus(other: Number) = AddSpek(this, ValueSpek(other))

operator fun <T> Spek<T, Number>.minus(other: Spek<T, Number>) = SubtractSpek(this, other)
operator fun <T> Spek<T, Number>.minus(other: Number) = SubtractSpek(this, ValueSpek(other))

class AddSpek<T>(private val lhs: Spek<T, out Number>, private val rhs: Spek<T, out Number>) :
    Spek<T, Number> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        return visiting(visitor) {
            lhs.evaluate(visitor, candidate).toDouble() + rhs.evaluate(visitor, candidate).toDouble()
        }
    }
}

class SubtractSpek<T>(private val lhs: Spek<T, Number>, private val rhs: Spek<T, Number>) : Spek<T, Number> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        return visiting(visitor) {
            lhs.evaluate(visitor, candidate).toDouble() - rhs.evaluate(visitor, candidate).toDouble()
        }
    }
}

class MultiplySpek<T>(private val lhs: Spek<T, out Number>, private val rhs: Spek<T, out Number>) : Spek<T, Number> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        return visiting(visitor) {
            lhs.evaluate(visitor, candidate).toDouble() * rhs.evaluate(visitor, candidate).toDouble()
        }
    }
}

class DivideSpek<T>(private val lhs: Spek<T, out Number>, private val rhs: Spek<T, out Number>) : Spek<T, Number> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
        return visiting(visitor) {
            lhs.evaluate(visitor, candidate).toDouble() / rhs.evaluate(visitor, candidate).toDouble()
        }
    }
}

class GreaterThanSpek(private val base: Spek<Number, Number>, private val allowEquals: Boolean = false) :
    Spek<Number, Boolean> {
    constructor(base: Number, allowEquals: Boolean = false) : this(ValueSpek(base), allowEquals)

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: Number): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                candidate.toDouble() >= base.evaluate(visitor, candidate).toDouble()
            } else {
                candidate.toDouble() > base.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class GreaterThanOperatorSpek<T>(
    private val lhs: Spek<T, Number>,
    private val rhs: Spek<T, Number>,
    private val allowEquals: Boolean = false
) : Spek<T, Boolean> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                lhs.evaluate(visitor, candidate).toDouble() >= rhs.evaluate(visitor, candidate).toDouble()
            } else {
                lhs.evaluate(visitor, candidate).toDouble() > rhs.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class LessThanSpek(private val base: Spek<Number, Number>, private val allowEquals: Boolean = false) :
    Spek<Number, Boolean> {
    constructor(base: Number, allowEquals: Boolean = false) : this(ValueSpek(base), allowEquals)

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: Number): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                candidate.toDouble() <= base.evaluate(visitor, candidate).toDouble()
            } else {
                candidate.toDouble() < base.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class LessThanOperatorSpek<T>(
    private val lhs: Spek<T, Number>,
    private val rhs: Spek<T, Number>,
    private val allowEquals: Boolean = false
) : Spek<T, Boolean> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Boolean {
        return visiting(visitor) {
            if (allowEquals) {
                lhs.evaluate(visitor, candidate).toDouble() <= rhs.evaluate(visitor, candidate).toDouble()
            } else {
                lhs.evaluate(visitor, candidate).toDouble() < rhs.evaluate(visitor, candidate).toDouble()
            }
        }
    }
}

class LargestSpek<T>(private vararg val bases: Spek<T, out Number>) : Spek<T, Number> {

    override val children = bases.toList()

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
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

class SmallestSpek<T>(private vararg val bases: Spek<T, Number>) : Spek<T, Number> {

    override val children = bases.toList()

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Number {
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