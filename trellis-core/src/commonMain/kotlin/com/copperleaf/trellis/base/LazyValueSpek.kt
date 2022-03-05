package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Treat a static value as the return value a spek. Value is lazily loaded.
 */
public class LazyValueSpek<Candidate, Result>(
    private val value: () -> Result
) : BaseSpek<Candidate, Result>() {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { value() }
    }
}
