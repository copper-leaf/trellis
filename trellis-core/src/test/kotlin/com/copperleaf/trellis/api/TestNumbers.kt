package com.copperleaf.trellis.api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TestNumbers {

    @Test
    fun testGreaterThan() = runBlocking<Unit> {
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 7)).isTrue()
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 5)).isFalse()

        expectThat(GreaterThanSpek(6, true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testLessThan() = runBlocking<Unit> {
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 7)).isFalse()
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 5)).isTrue()

        expectThat(LessThanSpek(6, true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testEqualsNumbers() = runBlocking<Unit> {
        expectThat(EqualsSpek(4).evaluate(EmptyVisitor, 12)).isFalse()
        expectThat(EqualsSpek(4).evaluate(EmptyVisitor, 4)).isTrue()
    }

    @Test
    fun testEqualsStrings() = runBlocking<Unit> {
        expectThat(EqualsSpek("asdf").evaluate(EmptyVisitor, "qwerty")).isFalse()
        expectThat(EqualsSpek("asdf").evaluate(EmptyVisitor, "asdf")).isTrue()
    }

    @Test
    fun testPlusSpek() = runBlocking<Unit> {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) + ValueSpek<Number, Number>(4))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 8)).isTrue()
    }

    @Test
    fun testPlusValue() = runBlocking<Unit> {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) + 4)

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 8)).isTrue()
    }

    @Test
    fun testMinusSpek() = runBlocking<Unit> {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) - ValueSpek<Number, Number>(2))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testMinusValue() = runBlocking<Unit> {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) - 2)

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testLargestWithPositiveValues() = runBlocking<Unit> {
        val input = 0
        val spek = LargestSpek<Number>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(3)
    }

    @Test
    fun testLargestWithNegativeValues() = runBlocking<Unit> {
        val input = 0
        val spek = LargestSpek<Number>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-1)
    }

    @Test
    fun testSmallestWithPositiveValues() = runBlocking<Unit> {
        val input = 0
        val spek = SmallestSpek<Number>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(1)
    }

    @Test
    fun testSmallestWithNegativeValues() = runBlocking<Unit> {
        val input = 0
        val spek = SmallestSpek<Number>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-3)
    }

}
