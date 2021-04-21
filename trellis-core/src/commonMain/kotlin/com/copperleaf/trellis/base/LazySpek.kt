package com.copperleaf.trellis.base

import com.copperleaf.trellis.util.printSpekTree
import com.copperleaf.trellis.visitor.SpekVisitor

class LazySpek<Candidate, Result> : Spek<Candidate, Result> {

    private lateinit var spek: Spek<Candidate, Result>

    override val spekName: String get() = spek.spekName
    override val children: List<Spek<*, *>>
        get() = listOf(spek)

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return spek.evaluate(visitor, candidate)
    }

    @Suppress("UNCHECKED_CAST")
    infix fun uses(spek: Spek<Candidate, Result>) {
        this.spek = spek
    }

    override fun toString(): String {
        return printSpekTree()
    }
}
