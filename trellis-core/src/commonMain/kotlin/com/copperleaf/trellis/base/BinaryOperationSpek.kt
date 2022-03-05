package com.copperleaf.trellis.base

import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

public open class BinaryOperationSpek<Candidate, Operand, Result>(
    private val lhs: Spek<Candidate, Operand>,
    private val rhs: Spek<Candidate, Operand>,
    private val cb: (() -> Operand, () -> Operand) -> Result
) : BaseSpek<Candidate, Result>(lhs, rhs) {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Result {
        return visiting(visitor) {
            val lVal: () -> Operand = { lhs.evaluate(visitor, candidate) }
            val rVal: () -> Operand = { rhs.evaluate(visitor, candidate) }
            cb(lVal, rVal)
        }
    }
}
