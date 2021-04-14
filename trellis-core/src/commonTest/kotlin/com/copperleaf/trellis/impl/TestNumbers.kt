package com.copperleaf.trellis.impl

import com.copperleaf.trellis.*
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import kotlin.test.Test

class TestNumbers {

    @Test
    fun testGreaterThan() {
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 7)).isTrue()
        expectThat(GreaterThanSpek(6).evaluate(EmptyVisitor, 5)).isFalse()

        expectThat(GreaterThanSpek(6, true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testLessThan() {
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 7)).isFalse()
        expectThat(LessThanSpek(6).evaluate(EmptyVisitor, 5)).isTrue()

        expectThat(LessThanSpek(6, true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testEqualsNumbers() {
        expectThat(EqualsSpek(4).evaluate(EmptyVisitor, 12)).isFalse()
        expectThat(EqualsSpek(4).evaluate(EmptyVisitor, 4)).isTrue()
    }

    @Test
    fun testEqualsStrings() {
        expectThat(EqualsSpek("asdf").evaluate(EmptyVisitor, "qwerty")).isFalse()
        expectThat(EqualsSpek("asdf").evaluate(EmptyVisitor, "asdf")).isTrue()
    }

    @Test
    fun testPlusSpek() {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) + ValueSpek<Number, Number>(4))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 8)).isTrue()
    }

    @Test
    fun testPlusValue() {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) + 4)

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 8)).isTrue()
    }

    @Test
    fun testMinusSpek() {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) - ValueSpek<Number, Number>(2))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testMinusValue() {
        val spek = EqualsSpek(ValueSpek<Number, Number>(4) - 2)

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testLargestWithPositiveValues() {
        val input = 0
        val spek = LargestSpek<Number>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(3)
    }

    @Test
    fun testLargestWithNegativeValues() {
        val input = 0
        val spek = LargestSpek<Number>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-1)
    }

    @Test
    fun testSmallestWithPositiveValues() {
        val input = 0
        val spek = SmallestSpek<Number>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(1)
    }

    @Test
    fun testSmallestWithNegativeValues() {
        val input = 0
        val spek = SmallestSpek<Number>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-3)
    }
}
