package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.base.BaseSpek
import com.copperleaf.trellis.visitor.SpekVisitor
import com.copperleaf.trellis.visitor.visiting

class StringLengthSpek : BaseSpek<String, Int>() {
    override fun evaluate(visitor: SpekVisitor, candidate: String): Int {
        return visiting(visitor) { candidate.length }
    }
}
