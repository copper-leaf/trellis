package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.api.Spek

fun Spek<*, *>.explore(visitor: SpekVisitor) {
    exploring(visitor) {
        this.children.forEach { it.explore(visitor) }
    }
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
        println("${indent(depth)}entering leaving $candidate")
        depth++
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        depth--
        println("${indent(depth)}leaving $candidate returned $result")
    }

    private fun indent(currentIndent: Int): String {
        return (0 until currentIndent).map { "| " }.joinToString(separator = "")
    }
}

class VisitorFilter(
    private val visitor: SpekVisitor,
    private val predicate: (Spek<*, *>) -> Boolean
) : SpekVisitor {

    override fun enter(candidate: Spek<*, *>) {
        if(predicate(candidate)) visitor.enter(candidate)
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        if(predicate(candidate)) visitor.leave(candidate, result)
    }
}