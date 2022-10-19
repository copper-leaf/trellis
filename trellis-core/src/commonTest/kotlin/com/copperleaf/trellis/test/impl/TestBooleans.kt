package com.copperleaf.trellis.test.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.Spek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.impl.booleans.and
import com.copperleaf.trellis.impl.booleans.andNot
import com.copperleaf.trellis.impl.booleans.not
import com.copperleaf.trellis.impl.booleans.or
import com.copperleaf.trellis.impl.booleans.orNot
import com.copperleaf.trellis.impl.comparison.EqualsSpek
import com.copperleaf.trellis.impl.strings.MaxLengthSpek
import com.copperleaf.trellis.impl.strings.MinLengthSpek
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestBooleans : StringSpec({

    "testMinLengthFail" {
        val input = "asdf"
        val spek = MinLengthSpek(ValueSpek(6))

        spek.evaluate(EmptyVisitor, input) shouldBe false
        spek.not().evaluate(EmptyVisitor, input) shouldBe true
    }

    "testMinLengthPass" {
        val input = "qwerty"
        val spek = MinLengthSpek(ValueSpek(6))

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

    "testEqualFail" {
        val input = "asdf"
        val expected = "qwerty"
        val spek = EqualsSpek(CandidateSpek(), ValueSpek(expected))

        spek.evaluate(EmptyVisitor, input) shouldBe false
        spek.not().evaluate(EmptyVisitor, input) shouldBe true
    }

    "testEqualPass" {
        val input = "asdf"
        val expected = "asdf"
        val spek = EqualsSpek(CandidateSpek(), ValueSpek(expected))

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

    "testMinLengthAndEqualFail" {
        val input = "asdf"
        val expected = "querty"
        val spek = MinLengthSpek(ValueSpek(6)) and EqualsSpek(CandidateSpek(), ValueSpek(expected))
        spek.evaluate(EmptyVisitor, input) shouldBe false
        spek.not().evaluate(EmptyVisitor, input) shouldBe true
    }

    "testMinLengthAndEqualPass" {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(ValueSpek(6)) and EqualsSpek(CandidateSpek(), ValueSpek(expected))

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

    "testMinLengthAndNotEqualFail" {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(ValueSpek(6)) andNot EqualsSpek(CandidateSpek(), ValueSpek(expected))

        spek.evaluate(EmptyVisitor, input) shouldBe false
        spek.not().evaluate(EmptyVisitor, input) shouldBe true
    }

    "testMinLengthAndNotEqualPass" {
        val input = "asdfasdf"
        val expected = "querty"
        val spek = MinLengthSpek(ValueSpek(6)) andNot EqualsSpek(CandidateSpek(), ValueSpek(expected))

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

    "testMinAndMaxLengthPass" {
        val input = "asdfasdf"
        val spek = MinLengthSpek(ValueSpek(6)) and MaxLengthSpek(ValueSpek(12))

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

    "testOrFail" {
        val input = "four"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
            .map { EqualsSpek(CandidateSpek(), ValueSpek(it)) }
            .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        spek.evaluate(EmptyVisitor, input) shouldBe false
        spek.not().evaluate(EmptyVisitor, input) shouldBe true
    }

    "testOrPass" {
        val input = "two"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
            .map { EqualsSpek(CandidateSpek(), ValueSpek(it)) }
            .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        spek.evaluate(EmptyVisitor, input) shouldBe true
        spek.not().evaluate(EmptyVisitor, input) shouldBe false
    }

// Operator overloading and infix method testing
// ---------------------------------------------------------------------------------------------------------------------

    "testOperator_base" {
        val input = "asdf"
        val spek = MinLengthSpek(ValueSpek(6))

        spek.evaluate(EmptyVisitor, input) shouldBe false
    }

    "testOperator_unaryMinus" {
        val input = "asdf"
        val spek = MinLengthSpek(ValueSpek(6)).not()

        spek.evaluate(EmptyVisitor, input) shouldBe true
    }

    "testOperator_plus" {
        val input = "asdfasdf"
        val spek = MinLengthSpek(ValueSpek(6)) and MaxLengthSpek(ValueSpek(12))

        spek.evaluate(EmptyVisitor, input) shouldBe true
    }

    "testOperator_plus_unaryMinus" {
        val input = "asdfasdf"
        val spek = MinLengthSpek(ValueSpek(6)) and MaxLengthSpek(ValueSpek(12)).not()

        spek.evaluate(EmptyVisitor, input) shouldBe false
    }

    "testOperator_and" {
        val input = "asdfasdf"
        val spek = MinLengthSpek(ValueSpek(6)) and MaxLengthSpek(ValueSpek(12))

        spek.evaluate(EmptyVisitor, input) shouldBe true
    }

    "testOperator_andNot" {
        val input = "asdfasdf"
        val spek = MinLengthSpek(ValueSpek(6)) andNot MaxLengthSpek(ValueSpek(12))

        spek.evaluate(EmptyVisitor, input) shouldBe false
    }

    "testOperator_or" {
        val input = "a"
        val spek = EqualsSpek(
            CandidateSpek(),
            ValueSpek("a")
        ) or EqualsSpek(
            CandidateSpek(),
            ValueSpek("b")
        )

        spek.evaluate(EmptyVisitor, input) shouldBe true
    }

    "testOperator_orNot" {
        val input = "b"
        val spek = EqualsSpek(
            CandidateSpek(),
            ValueSpek("a")
        ) orNot EqualsSpek(
            CandidateSpek(),
            ValueSpek("b")
        )

        spek.evaluate(EmptyVisitor, input) shouldBe false
    }
})
