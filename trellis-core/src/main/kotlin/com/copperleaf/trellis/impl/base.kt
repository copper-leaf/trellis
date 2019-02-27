package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting

/**
 * Treat a static value as the return value a spek. Value is lazily loaded.
 */
class ValueSpek<T, U>(private val value: suspend () -> U) : Spek<T, U> {
    constructor(value: U) : this({ value })

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { value() }
    }
}

/**
 * Treat a candidate as the return value of a Spek.
 */
class CandidateSpek<T> : Spek<T, T> {
    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): T {
        return visiting(visitor) { candidate }
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
    constructor(value: suspend () -> V, base: Spek<V, U>) : this(ValueSpek(value), base)

    override val children = listOf(newCandidate, base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { base.evaluate(visitor, newCandidate.evaluate(visitor, candidate)) }
    }
}

/**
 * Wrap a Spek
 */
open class SpekWrapper<T, U>(private val base: Spek<T, U>) : Spek<T, U> {
    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): U {
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
    private val cb: suspend (suspend () -> U, suspend () -> U) -> V
) : Spek<T, V> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            val lVal: suspend () -> U = { lhs.evaluate(visitor, candidate) }
            val rVal: suspend () -> U = { rhs.evaluate(visitor, candidate) }
            cb(lVal, rVal)
        }
    }
}

class UnaryOperationSpek<T, U, V>(
    private val base: Spek<T, U>,
    private val cb: suspend (suspend () -> U) -> V
) : Spek<T, V> {

    override val children = listOf(base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): V {
        return visiting(visitor) {
            cb { base.evaluate(visitor, candidate) }
        }
    }

}