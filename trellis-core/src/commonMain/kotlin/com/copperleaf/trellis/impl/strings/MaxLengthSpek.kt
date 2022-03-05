package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.base.BaseSpek
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

public class MaxLengthSpek(
    private val maxLength: Spek<String, Int>
) : BaseSpek<String, Boolean>(maxLength) {

    override fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) {
            candidate.length <= maxLength.evaluate(visitor, candidate)
        }
    }
}
