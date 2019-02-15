package com.copperleaf.trellis.api

interface Spek<T, U> {
    suspend fun evaluate(candidate: T): U
}

/**
 * Treat a static value as the return value a spek. Value is lazily loaded.
 */
class ValueSpek<T, U>(private val value: suspend () -> U) : Spek<T, U> {
    constructor(value: U) : this({ value })

    override suspend fun evaluate(candidate: T): U {
        return value()
    }
}

/**
 * Treat a candidate as the return value of a Spek.
 */
class CandidateSpek<T> : Spek<T, T> {
    override suspend fun evaluate(candidate: T): T {
        return candidate
    }
}

/**
 * Check that two Speks have equal values. Number values are converted to doubles before checking equality.
 */
class EqualsSpek<T>(private val base: Spek<T, T>) : Spek<T, Boolean> {
    constructor(base: T) : this(ValueSpek(base))

    override suspend fun evaluate(candidate: T): Boolean {
        val a = candidate
        val b = base.evaluate(candidate)

        return if (a is Number && b is Number) {
            a.toDouble() == b.toDouble()
        } else {
            a == b
        }
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

    override suspend fun evaluate(candidate: T): U {
        return base.evaluate(newCandidate.evaluate(candidate))
    }
}
