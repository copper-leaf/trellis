package com.copperleaf.trellis.impl

import com.copperleaf.trellis.*
import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import kotlin.test.Test

class TestBooleans {

    @Test
    fun testMinLengthFail() {
        val input = "asdf"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthPass() {
        val input = "qwerty"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testEqualFail() {
        val input = "asdf"
        val expected = "qwerty"
        val spek = EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testEqualPass() {
        val input = "asdf"
        val expected = "asdf"
        val spek = EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinLengthAndEqualFail() {
        val input = "asdf"
        val expected = "querty"
        val spek = MinLengthSpek(6) and EqualsSpek(expected)
        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthAndEqualPass() {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(6) and EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinLengthAndNotEqualFail() {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(6) andNot EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthAndNotEqualPass() {
        val input = "asdfasdf"
        val expected = "querty"
        val spek = MinLengthSpek(6) andNot EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinAndMaxLengthPass() {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) and MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOrFail() {
        val input = "four"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
            .map { EqualsSpek(it) }
            .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOrPass() {
        val input = "two"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
            .map { EqualsSpek(it) }
            .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

// Operator overloading and infix method testing
// ---------------------------------------------------------------------------------------------------------------------

    @Test
    fun testOperator_base() {
        val input = "asdf"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_unaryMinus() {
        val input = "asdf"
        val spek = -MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_plus() {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) + MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_plus_unaryMinus() {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) + -MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_and() {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) and MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_andNot() {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) andNot MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_or() {
        val input = "a"
        val spek = EqualsSpek("a") or EqualsSpek("b")

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_orNot() {
        val input = "b"
        val spek = EqualsSpek("a") orNot EqualsSpek("b")

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }
}
