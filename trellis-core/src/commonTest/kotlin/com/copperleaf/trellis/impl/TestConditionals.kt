package com.copperleaf.trellis.impl

import com.copperleaf.trellis.*
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import kotlin.test.Test

class TestConditionals {

    @Test
    fun testIf() {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        expectThat(spek.evaluate(EmptyVisitor, true)).isEqualTo("pass")
        expectThat(spek.evaluate(EmptyVisitor, false)).isEqualTo("fail")
    }
}
