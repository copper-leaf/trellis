@file:Suppress("UNCHECKED_CAST")

package com.copperleaf.trellis.impl.comparison

import com.copperleaf.trellis.base.BaseSpek
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

class MinSpek<Candidate, Operand : Comparable<Operand>>(
    private vararg val bases: Spek<Candidate, Operand>
) : BaseSpek<Candidate, Operand>(*bases) {

    override fun evaluate(visitor: SpekVisitor, candidate: Candidate): Operand {
        return visiting(visitor) {
            bases
                .asSequence()
                .minOf { it.evaluate(visitor, candidate) }
        }
    }
}
