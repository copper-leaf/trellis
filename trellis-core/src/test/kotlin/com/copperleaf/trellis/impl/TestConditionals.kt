package com.copperleaf.trellis.impl

import com.copperleaf.trellis.impl.CandidateSpek
import com.copperleaf.trellis.impl.ValueSpek
import com.copperleaf.trellis.impl.then
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TestConditionals {

    @Test
    fun testIf() = runBlocking<Unit> {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        expectThat(spek.evaluate(EmptyVisitor, true)).isEqualTo("pass")
        expectThat(spek.evaluate(EmptyVisitor, false)).isEqualTo("fail")
    }

}
