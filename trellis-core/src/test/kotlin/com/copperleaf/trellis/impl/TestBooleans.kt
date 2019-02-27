package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TestBooleans {

    @Test
    fun testMinLengthFail() = runBlocking<Unit> {
        val input = "asdf"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthPass() = runBlocking<Unit> {
        val input = "qwerty"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testEqualFail() = runBlocking<Unit> {
        val input = "asdf"
        val expected = "qwerty"
        val spek = EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testEqualPass() = runBlocking<Unit> {
        val input = "asdf"
        val expected = "asdf"
        val spek = EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinLengthAndEqualFail() = runBlocking<Unit> {
        val input = "asdf"
        val expected = "querty"
        val spek = MinLengthSpek(6) and EqualsSpek(expected)
        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthAndEqualPass() = runBlocking<Unit> {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(6) and EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinLengthAndNotEqualFail() = runBlocking<Unit> {
        val input = "querty"
        val expected = "querty"
        val spek = MinLengthSpek(6) andNot EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testMinLengthAndNotEqualPass() = runBlocking<Unit> {
        val input = "asdfasdf"
        val expected = "querty"
        val spek = MinLengthSpek(6) andNot EqualsSpek(expected)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testMinAndMaxLengthPass() = runBlocking<Unit> {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) and MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOrFail() = runBlocking<Unit> {
        val input = "four"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
                .map { EqualsSpek(it) }
                .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOrPass() = runBlocking<Unit> {
        val input = "two"
        val expected = arrayOf("one", "two", "three")

        val spek = expected
                .map { EqualsSpek(it) }
                .reduce { acc: Spek<String, Boolean>, next: Spek<String, Boolean> -> acc or next }

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
        expectThat(spek.not().evaluate(EmptyVisitor, input)).isFalse()
    }

// Operator overloading and infix method testing
//----------------------------------------------------------------------------------------------------------------------

    @Test
    fun testOperator_base() = runBlocking<Unit> {
        val input = "asdf"
        val spek = MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_unaryMinus() = runBlocking<Unit> {
        val input = "asdf"
        val spek = -MinLengthSpek(6)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_plus() = runBlocking<Unit> {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) + MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_plus_unaryMinus() = runBlocking<Unit> {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) + -MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_and() = runBlocking<Unit> {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) and MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_andNot() = runBlocking<Unit> {
        val input = "asdfasdf"
        val spek = MinLengthSpek(6) andNot MaxLengthSpek(12)

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

    @Test
    fun testOperator_or() = runBlocking<Unit> {
        val input = "a"
        val spek = EqualsSpek("a") or EqualsSpek("b")

        expectThat(spek.evaluate(EmptyVisitor, input)).isTrue()
    }

    @Test
    fun testOperator_orNot() = runBlocking<Unit> {
        val input = "b"
        val spek = EqualsSpek("a") orNot EqualsSpek("b")

        expectThat(spek.evaluate(EmptyVisitor, input)).isFalse()
    }

}
