package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Treat a candidate as the return value of a Spek.
 */
public class CandidateSpek<Candidate> : BaseSpek<Candidate, Candidate>() {
    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Candidate {
        return visiting(visitor) { candidate }
    }
}
