package com.copperleaf.trellis.api

interface Spek<T, U> {
    suspend fun evaluate(visitor: SpekVisitor, candidate: T): U
    val children: List<Spek<*, *>>
        get() = emptyList()
}

fun Spek<*, *>.explore(visitor: SpekVisitor) {
    exploring(visitor) {
        this.children.forEach { it.explore(visitor) }
    }
}

interface SpekVisitor {
    fun enter(candidate: Spek<*, *>)
    fun <U> leave(candidate: Spek<*, *>, result: U)
}

suspend fun <U> Spek<*, *>.visiting(visitor: SpekVisitor, cb: suspend Spek<*, *>.() -> U): U {
    visitor.enter(this)
    val result = this.cb()
    return result.also { visitor.leave(this, it) }
}
fun Spek<*, *>.exploring(visitor: SpekVisitor, cb: Spek<*, *>.() -> Unit) {
    visitor.enter(this)
    this.cb()
    visitor.leave(this, null)
}

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

class EqualsOperatorSpek<T>(
    private val lhs: Spek<T, T>,
    private val rhs: Spek<T, T>,
    private val strict: Boolean = false
) : Spek<T, Boolean> {

    override val children = listOf(lhs, rhs)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): Boolean {
        return visiting(visitor) {
            val a = lhs.evaluate(visitor, candidate)
            val b = rhs.evaluate(visitor, candidate)

            if (strict) {
                a === b
            } else {
                if (a is Number && b is Number) {
                    a.toDouble() == b.toDouble()
                } else {
                    a == b
                }
            }
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

    override val children = listOf(newCandidate, base)

    override suspend fun evaluate(visitor: SpekVisitor, candidate: T): U {
        return visiting(visitor) { base.evaluate(visitor, newCandidate.evaluate(visitor, candidate)) }
    }
}

/**
 * The default Visitor, which does nothing when a Spek is visited
 */
object EmptyVisitor : SpekVisitor {
    override fun enter(candidate: Spek<*, *>) {

    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {

    }
}

/**
 * A simple Visitor which simply prints the name of the Spek being visited and its result.
 */
class PrintVisitor : SpekVisitor {

    private var depth: Int = 0

    override fun enter(candidate: Spek<*, *>) {
        println("${indent(depth)}entering leaving ${candidate.javaClass.simpleName}")
        depth++
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        depth--
        println("${indent(depth)}leaving ${candidate.javaClass.simpleName} returned $result")
    }

    private fun indent(currentIndent: Int): String {
        return (0 until currentIndent).map { "| " }.joinToString(separator = "")
    }

}