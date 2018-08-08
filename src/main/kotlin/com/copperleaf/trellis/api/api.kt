package com.copperleaf.trellis.api



interface Spek<T, U> {
    suspend fun evaluate(candidate: T): U
}

class ValueSpek<T, U>(private val value: ()->U) : Spek<T, U> {
    constructor(value: U) : this({value})

    override suspend fun evaluate(candidate: T): U {
        return value()
    }
}

class CandidateSpek<T> : Spek<T, T> {
    override suspend fun evaluate(candidate: T): T {
        return candidate
    }
}

class EqualsSpek<T>(private val base: Spek<T, T>) : Spek<T, Boolean> {
    constructor(base: T) : this(ValueSpek(base))

    override suspend fun evaluate(candidate: T): Boolean {
        val a = candidate
        val b = base.evaluate(candidate)

        return if (a is Number && b is Number) {
            a.toDouble() == b.toDouble()
        }
        else {
            a == b
        }
    }
}
