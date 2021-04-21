package com.copperleaf.trellis.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.MapCandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.expectThat
import com.copperleaf.trellis.impl.comparison.EqualsSpek
import com.copperleaf.trellis.impl.comparison.GreaterThanSpek
import com.copperleaf.trellis.impl.comparison.LessThanSpek
import com.copperleaf.trellis.impl.comparison.MaxSpek
import com.copperleaf.trellis.impl.comparison.MinSpek
import com.copperleaf.trellis.impl.math.minus
import com.copperleaf.trellis.impl.math.plus
import com.copperleaf.trellis.isEqualTo
import com.copperleaf.trellis.isFalse
import com.copperleaf.trellis.isTrue
import com.copperleaf.trellis.visitor.EmptyVisitor
import kotlin.test.Test

class TestNumbers {

    @Test
    fun testGreaterThan() {
        expectThat(GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 7)).isTrue()
        expectThat(GreaterThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 5)).isFalse()

        expectThat(GreaterThanSpek(CandidateSpek(), ValueSpek(6), true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testLessThan() {
        expectThat(LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 6)).isFalse()
        expectThat(LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 7)).isFalse()
        expectThat(LessThanSpek(CandidateSpek(), ValueSpek(6), false).evaluate(EmptyVisitor, 5)).isTrue()

        expectThat(LessThanSpek(CandidateSpek(), ValueSpek(6), true).evaluate(EmptyVisitor, 6)).isTrue()
    }

    @Test
    fun testEqualsNumbers() {
        expectThat(EqualsSpek(CandidateSpek(), ValueSpek(4)).evaluate(EmptyVisitor, 12)).isFalse()
        expectThat(EqualsSpek(CandidateSpek(), ValueSpek(4)).evaluate(EmptyVisitor, 4)).isTrue()
    }

    @Test
    fun testEqualsStrings() {
        expectThat(EqualsSpek(CandidateSpek(), ValueSpek("asdf")).evaluate(EmptyVisitor, "qwerty")).isFalse()
        expectThat(EqualsSpek(CandidateSpek(), ValueSpek("asdf")).evaluate(EmptyVisitor, "asdf")).isTrue()
    }

    @Test
    fun testPlusSpek() {
        val spek = EqualsSpek(MapCandidateSpek { it.toDouble() }, ValueSpek<Int, Int>(4) + ValueSpek(4))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 8)).isTrue()
    }

    @Test
    fun testMinusSpek() {
        val spek = EqualsSpek(MapCandidateSpek { it.toDouble() }, ValueSpek<Int, Int>(4) - ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, 4)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testLargestWithPositiveValues() {
        val input = 0
        val spek = MaxSpek<Int, Int>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(3)
    }

    @Test
    fun testLargestWithNegativeValues() {
        val input = 0
        val spek = MaxSpek<Int, Int>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-1)
    }

    @Test
    fun testSmallestWithPositiveValues() {
        val input = 0
        val spek = MinSpek<Int, Int>(ValueSpek(1), ValueSpek(3), ValueSpek(2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(1)
    }

    @Test
    fun testSmallestWithNegativeValues() {
        val input = 0
        val spek = MinSpek<Int, Int>(ValueSpek(-1), ValueSpek(-3), ValueSpek(-2))

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(-3)
    }
}
