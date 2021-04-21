package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor

interface Spek<Candidate, Result> {

    val spekName: String
    val children: List<Spek<*, *>>

    fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result
}
