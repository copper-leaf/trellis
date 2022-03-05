package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

/**
 * Treat a static value as the return value a spek. Value is eagerly loaded. For values that should be loaded lazily
 * or recomputed on each evaluation, see [LazyValueSpek].
 */
public class ValueSpek<Candidate, Result>(
    private val value: Result
) : BaseSpek<Candidate, Result>() {

    override val spekName: String = "$value"

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) { value }
    }
}
