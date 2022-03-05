package com.copperleaf.trellis.visitor

import com.copperleaf.trellis.base.Spek

public interface SpekVisitor {
    public fun enter(candidate: Spek<*, *>)
    public fun <Result> leave(candidate: Spek<*, *>, result: Result)
}

public fun Spek<*, *>.explore(visitor: SpekVisitor) {
    exploring(visitor) {
        this.children.forEach { it.explore(visitor) }
    }
}

public fun <Result> Spek<*, *>.visiting(visitor: SpekVisitor, cb: Spek<*, *>.() -> Result): Result {
    visitor.enter(this)
    val result = this.cb()
    return result.also { visitor.leave(this, it) }
}

public fun Spek<*, *>.exploring(visitor: SpekVisitor, cb: Spek<*, *>.() -> Unit) {
    visitor.enter(this)
    this.cb()
    visitor.leave(this, null)
}
