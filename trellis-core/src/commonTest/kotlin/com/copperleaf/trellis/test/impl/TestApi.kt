package com.copperleaf.trellis.test.impl

import com.copperleaf.trellis.base.AlsoSpek
import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.comparison.EqualsSpek
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestApi : StringSpec({
    "testValueSpek" {
        val input = 1
        val spek = ValueSpek<Int, Int>(2)

        spek.evaluate(EmptyVisitor, input) shouldBe 2
    }

    "testCandidateSpek" {
        val input = 1
        val spek = CandidateSpek<Int>()

        spek.evaluate(EmptyVisitor, input) shouldBe 1
    }

    "testEqualsSpekInt" {
        val spek = EqualsSpek(CandidateSpek(), ValueSpek(2))

        spek.evaluate(EmptyVisitor, 1) shouldBe false
        spek.evaluate(EmptyVisitor, 2) shouldBe true
    }

    "testEqualsSpekDouble" {
        val spek = EqualsSpek(CandidateSpek(), ValueSpek(2.2))

        spek.evaluate(EmptyVisitor, 1.0) shouldBe false
        spek.evaluate(EmptyVisitor, 2.0) shouldBe false
        spek.evaluate(EmptyVisitor, 2.2) shouldBe true
    }

    "testAlsoSpek" {
        // Test with a value
        AlsoSpek<Double, Double, Boolean>(ValueSpek(1.0), EqualsSpek(CandidateSpek(), ValueSpek(2.2))).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe false

        AlsoSpek<Double, Double, Boolean>(ValueSpek(2.0), EqualsSpek(CandidateSpek(), ValueSpek(2.2))).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe false

        AlsoSpek<Double, Double, Boolean>(ValueSpek(2.2), EqualsSpek(CandidateSpek(), ValueSpek(2.2))).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe true

        // test with a lambda
        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(1.0),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(EmptyVisitor, 2.2) shouldBe false

        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(2.0),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(EmptyVisitor, 2.2) shouldBe false

        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(2.2),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(EmptyVisitor, 2.2) shouldBe true

        // test with a Spek
        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(1.0),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe false

        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(2.0),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe false

        AlsoSpek<Double, Double, Boolean>(
            ValueSpek(2.2),
            EqualsSpek(CandidateSpek(), ValueSpek(2.2))
        ).evaluate(
            EmptyVisitor,
            2.2
        ) shouldBe true
    }
})
