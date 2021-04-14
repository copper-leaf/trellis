package com.copperleaf.trellis.impl

import com.copperleaf.trellis.expectThat
import com.copperleaf.trellis.introspection.visitor.EmptyVisitor
import com.copperleaf.trellis.isEqualTo
import com.copperleaf.trellis.isFalse
import com.copperleaf.trellis.isTrue
import kotlin.test.Test

class TestApi {

    @Test
    fun testValueSpek() {
        val input = 1
        val spek = ValueSpek<Int, Int>(2)

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(2)
    }

    @Test
    fun testCandidateSpek() {
        val input = 1
        val spek = CandidateSpek<Int>()

        expectThat(spek.evaluate(EmptyVisitor, input)).isEqualTo(1)
    }

    @Test
    fun testEqualsSpekInt() {
        val spek = EqualsSpek(2)

        expectThat(spek.evaluate(EmptyVisitor, 1)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2)).isTrue()
    }

    @Test
    fun testEqualsSpekDouble() {
        val spek = EqualsSpek(2.2)

        expectThat(spek.evaluate(EmptyVisitor, 1.0)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2.0)).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, 2.2)).isTrue()
    }

    @Test
    fun testAlsoSpek() {
        // Test with a value
        expectThat(AlsoSpek<Double, Double, Boolean>(1.0, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isFalse()
        expectThat(AlsoSpek<Double, Double, Boolean>(2.0, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isFalse()
        expectThat(AlsoSpek<Double, Double, Boolean>(2.2, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isTrue()

        // test with a lambda
        expectThat(AlsoSpek<Double, Double, Boolean>({ 1.0 }, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isFalse()
        expectThat(AlsoSpek<Double, Double, Boolean>({ 2.0 }, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isFalse()
        expectThat(AlsoSpek<Double, Double, Boolean>({ 2.2 }, EqualsSpek(2.2)).evaluate(EmptyVisitor, 2.2)).isTrue()

        // test with a Spek
        expectThat(
            AlsoSpek<Double, Double, Boolean>(ValueSpek(1.0), EqualsSpek(2.2)).evaluate(
                EmptyVisitor,
                2.2
            )
        ).isFalse()
        expectThat(
            AlsoSpek<Double, Double, Boolean>(ValueSpek(2.0), EqualsSpek(2.2)).evaluate(
                EmptyVisitor,
                2.2
            )
        ).isFalse()
        expectThat(
            AlsoSpek<Double, Double, Boolean>(ValueSpek(2.2), EqualsSpek(2.2)).evaluate(
                EmptyVisitor,
                2.2
            )
        ).isTrue()
    }
}
