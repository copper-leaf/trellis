package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

/**
 * Treat a static value as the return value a spek. Value is lazily loaded.
 */
class ValueSpek<T, U>(private val value: () -> U) : Spek<T, U> {
    constructor(value: U) : this({ value })

    override fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { value() }
    }

    override fun toString(): String {
        return "ValueSpek()"
    }
}

/**
 * Treat a candidate as the return value of a Spek.
 */
class CandidateSpek<T> : Spek<T, T> {
    override fun evaluate(visitor: SpekVisitor, candidate: T): T {
        return visiting(visitor) { candidate }
    }

    override fun toString(): String {
        return "CandidateSpek()"
    }
}

/**
 * Adds a new candidate to the Spek chain, to be evaluated by a matching Spek. This allows a single Spek to be evaluated
 * with multiple disparate input argument candidates, where each candidate has its own evaluation subtree. The new
 * candidate is itself a Spek, so can pass the result of one subtree to a new subtree, or you can just pass a value or
 * lambda directly as the value.
 */
class AlsoSpek<T, V, U>(private val newCandidate: Spek<T, V>, private val base: Spek<V, U>) : Spek<T, U> {
    constructor(value: V, base: Spek<V, U>) : this(ValueSpek(value), base)
    constructor(value: () -> V, base: Spek<V, U>) : this(ValueSpek(value), base)

    override val children = listOf(newCandidate, base)

    override fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { base.evaluate(visitor, newCandidate.evaluate(visitor, candidate)) }
    }

    override fun toString(): String {
        return "AlsoSpek()"
    }
}

/**
 * Wrap a Spek
 */
open class SpekWrapper<T, U>(private val base: Spek<T, U>) : Spek<T, U> {
    override val children = listOf(base)

    override fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { base.evaluate(visitor, candidate) }
    }
}

/**
 * Wrap a spek and give it a name
 */
open class NamedSpek<T, U>(private val name: String, base: Spek<T, U>) : SpekWrapper<T, U>(base) {
    override fun toString(): String {
        return name
    }
}

fun <T, U> Spek<T, U>.named(name: String): Spek<T, U> = NamedSpek(name, this)

class BinaryOperationSpek<T, U, V>(
    private val lhs: Spek<T, U>,
    private val rhs: Spek<T, U>,
    private val name: String? = null,
    private val cb: (() -> U, () -> U) -> V
) : Spek<T, V> {

    override val children = listOf(lhs, rhs)

    override fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            val lVal: () -> U = { lhs.evaluate(visitor, candidate) }
            val rVal: () -> U = { rhs.evaluate(visitor, candidate) }
            cb(lVal, rVal)
        }
    }

    override fun toString(): String {
        return name ?: super.toString()
    }

    companion object
}

class UnaryOperationSpek<T, U, V>(
    private val base: Spek<T, U>,
    private val name: String? = null,
    private val cb: (() -> U) -> V
) : Spek<T, V> {

    override val children = listOf(base)

    override fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            cb { base.evaluate(visitor, candidate) }
        }
    }

    override fun toString(): String {
        return name ?: super.toString()
    }

    companion object
}
