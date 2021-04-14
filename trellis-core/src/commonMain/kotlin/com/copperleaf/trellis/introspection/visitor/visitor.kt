package com.copperleaf.trellis.introspection.visitor

import com.copperleaf.trellis.api.Spek

fun Spek<*, *>.explore(visitor: SpekVisitor) {
    exploring(visitor) {
        this.children.forEach { it.explore(visitor) }
    }
}

fun <U> Spek<*, *>.visiting(visitor: SpekVisitor, cb: Spek<*, *>.() -> U): U {
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

class VisitorFilter(
    private val visitor: SpekVisitor,
    private val predicate: (Spek<*, *>) -> Boolean
) : SpekVisitor {

    override fun enter(candidate: Spek<*, *>) {
        if (predicate(candidate)) visitor.enter(candidate)
    }

    override fun <U> leave(candidate: Spek<*, *>, result: U) {
        if (predicate(candidate)) visitor.leave(candidate, result)
    }
}
