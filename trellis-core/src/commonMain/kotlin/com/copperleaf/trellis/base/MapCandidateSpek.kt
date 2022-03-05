package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Treat a candidate as the return value of a Spek.
 */
public class MapCandidateSpek<Candidate, Result>(
    private val mapper: (Candidate) -> Result
) : BaseSpek<Candidate, Result>() {
    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { mapper(candidate) }
    }
}
