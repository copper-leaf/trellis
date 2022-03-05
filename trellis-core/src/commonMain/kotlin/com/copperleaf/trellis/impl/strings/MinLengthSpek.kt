package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.base.BaseSpek
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

public class MinLengthSpek(
    private val minLength: Spek<String, Int>
) : BaseSpek<String, Boolean>(minLength) {

    override fun evaluate(visitor: SpekVisitor, candidate: String): Boolean {
        return visiting(visitor) {
            candidate.length >= minLength.evaluate(visitor, candidate)
        }
    }
}
