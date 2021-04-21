package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Adds a new candidate to the Spek chain, to be evaluated by a matching Spek. This allows a single Spek to be evaluated
 * with multiple disparate input argument candidates, where each candidate has its own evaluation subtree. The new
 * candidate is itself a Spek, so can pass the result of one subtree to a new subtree, or you can just pass a value or
 * lambda directly as the value.
 */
class AlsoSpek<Candidate, NewCandidate, Result>(
    private val newCandidate: Spek<Candidate, NewCandidate>,
    private val base: Spek<NewCandidate, Result>
) : BaseSpek<Candidate, Result>(newCandidate, base) {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { base.evaluate(visitor, newCandidate.evaluate(visitor, candidate)) }
    }
}
