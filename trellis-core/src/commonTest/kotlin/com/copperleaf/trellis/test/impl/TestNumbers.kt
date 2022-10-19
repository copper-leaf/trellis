package com.copperleaf.trellis.test.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.MapCandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.comparison.EqualsSpek
import com.copperleaf.trellis.impl.comparison.GreaterThanSpek
import com.copperleaf.trellis.impl.comparison.LessThanSpek
import com.copperleaf.trellis.impl.comparison.MaxSpek
import com.copperleaf.trellis.impl.comparison.MinSpek
import com.copperleaf.trellis.impl.math.minus
import com.copperleaf.trellis.impl.math.plus
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestNumbers : StringSpec({

    "testGreaterThan" {
        GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 6) shouldBe false
        GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 7) shouldBe true
        GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 5) shouldBe false

        GreaterThanSpek(CandidateSpek(), ValueSpek(6), true).evaluate(EmptyVisitor, 6) shouldBe true
    }

    "testLessThan" {
        LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 6) shouldBe false
        LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 7) shouldBe false
        LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 5) shouldBe true

        LessThanSpek(CandidateSpek(), ValueSpek(6), true).evaluate(EmptyVisitor, 6) shouldBe true
    }

    "testEqualsNumbers" {
        EqualsSpek(CandidateSpek(), ValueSpek(4)).evaluate(EmptyVisitor, 12) shouldBe false
        EqualsSpek(CandidateSpek(), ValueSpek(4)).evaluate(EmptyVisitor, 4) shouldBe true
    }

    "testEqualsStrings" {
        EqualsSpek(CandidateSpek(), ValueSpek("asdf")).evaluate(EmptyVisitor, "qwerty") shouldBe false
        EqualsSpek(CandidateSpek(), ValueSpek("asdf")).evaluate(EmptyVisitor, "asdf") shouldBe true
    }

    "testPlusSpek" {
        val spek = EqualsSpek(MapCandidateSpek { it.toDouble() }, ValueSpek<Int, Int>(4) + ValueSpek(4))

        spek.evaluate(EmptyVisitor, 4) shouldBe false
        spek.evaluate(EmptyVisitor, 8) shouldBe true
    }

    "testMinusSpek" {
        val spek = EqualsSpek(MapCandidateSpek { it.toDouble() }, ValueSpek<Int, Int>(4) - ValueSpek(2))

        spek.evaluate(EmptyVisitor, 4) shouldBe false
        spek.evaluate(EmptyVisitor, 2) shouldBe true
    }

    "testLargestWithPositiveValues" {
        val input = 0
        val spek = MaxSpek<Int, Int>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        spek.evaluate(EmptyVisitor, input) shouldBe 3
    }

    "testLargestWithNegativeValues" {
        val input = 0
        val spek = MaxSpek<Int, Int>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        spek.evaluate(EmptyVisitor, input) shouldBe -1
    }

    "testSmallestWithPositiveValues" {
        val input = 0
        val spek = MinSpek<Int, Int>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        spek.evaluate(EmptyVisitor, input) shouldBe 1
    }

    "testSmallestWithNegativeValues" {
        val input = 0
        val spek = MinSpek<Int, Int>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        spek.evaluate(EmptyVisitor, input) shouldBe -3
    }
})
