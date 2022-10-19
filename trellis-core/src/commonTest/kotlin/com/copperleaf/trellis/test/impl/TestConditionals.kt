package com.copperleaf.trellis.test.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.conditionals.then
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestConditionals : StringSpec({

    "testIf" {
        val spek = CandidateSpek<Boolean>().then(ValueSpek("pass"), ValueSpek("fail"))

        spek.evaluate(EmptyVisitor, true) shouldBe "pass"
        spek.evaluate(EmptyVisitor, false) shouldBe "fail"
    }
})
