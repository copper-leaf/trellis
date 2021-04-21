package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

open class UnaryOperationSpek<Candidate, Operand, Result>(
    private val base: Spek<Candidate, Operand>,
    private val name: String? = null,
    private val cb: (() -> Operand) -> Result
) : BaseSpek<Candidate, Result>(base) {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) {
            val baseVal: () -> Operand = { base.evaluate(visitor, candidate) }
            cb(baseVal)
        }
    }
}
