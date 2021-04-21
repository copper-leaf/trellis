package com.copperleaf.trellis.visitor

import com.copperleaf.trellis.base.Spek

interface SpekVisitor {
    fun enter(candidate: Spek<*, *>)
    fun <Result> leave(candidate: Spek<*, *>, result: Result)
}

fun Spek<*, *>.explore(visitor: SpekVisitor) {
    exploring(visitor) {
        this.children.forEach { it.explore(visitor) }
    }
}

fun <Result> Spek<*, *>.visiting(visitor: SpekVisitor, cb: Spek<*, *>.() -> Result): Result {
    visitor.enter(this)
    val result = this.cb()
    return result.also { visitor.leave(this, it) }
}

fun Spek<*, *>.exploring(visitor: SpekVisitor, cb: Spek<*, *>.() -> Unit) {
    visitor.enter(this)
    this.cb()
    visitor.leave(this, null)
}
