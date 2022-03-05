package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor

public interface Spek<Candidate, Result> {

    public val spekName: String
    public val children: List<Spek<*, *>>

    public fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result
}
