package com.copperleaf.trellis.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.expectThat
import com.copperleaf.trellis.impl.conditionals.then
import com.copperleaf.trellis.isEqualTo
import com.copperleaf.trellis.visitor.EmptyVisitor
import kotlin.test.Test

class TestConditionals {

    @Test
    fun testIf() {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        expectThat(spek.evaluate(EmptyVisitor, true)).isEqualTo("pass")
        expectThat(spek.evaluate(EmptyVisitor, false)).isEqualTo("fail")
    }
}
