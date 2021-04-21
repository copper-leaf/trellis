package com.copperleaf.trellis.visitor

import com.copperleaf.trellis.base.Spek

/**
 * The default Visitor, which does nothing when a Spek is visited
 */
object EmptyVisitor : SpekVisitor {
    override fun enter(candidate: Spek<*, *>) {
    }

    override fun <Result> leave(candidate: Spek<*, *>, result: Result) {
    }
}
