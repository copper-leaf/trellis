package com.copperleaf.trellis.api

import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.ExpectationBuilder
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TestApi {

    @Test
    fun testValueSpek() = runBlocking<Unit> {
        val input = 1
        val spek = ValueSpek<Int, Int>(2)

        expectThat(spek.evaluate(input)).isEqualTo(2)
    }

    @Test
    fun testCandidateSpek() = runBlocking<Unit> {
        val input = 1
        val spek = CandidateSpek<Int>()

        expectThat(spek.evaluate(input)).isEqualTo(1)
    }

    @Test
    fun testEqualsSpekInt() {
        val spek = EqualsSpek(2)

        expectCoroutine {
            that(spek.evaluate(1)).isFalse()
            that(spek.evaluate(2)).isTrue()
        }
    }

    @Test
    fun testEqualsSpekDouble() {
        val spek = EqualsSpek(2.2)

        expectCoroutine {
            that(spek.evaluate(1.0)).isFalse()
            that(spek.evaluate(2.0)).isFalse()
            that(spek.evaluate(2.2)).isTrue()
        }
    }

    @Test
    fun testAlsoSpek() {
        expectCoroutine {
            // Test with a value
            that(AlsoSpek<Double, Double, Boolean>(1.0, EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>(2.0, EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>(2.2, EqualsSpek(2.2)).evaluate(2.2)).isTrue()

            // test with a lambda
            that(AlsoSpek<Double, Double, Boolean>({1.0}, EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>({2.0}, EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>({2.2}, EqualsSpek(2.2)).evaluate(2.2)).isTrue()

            // test with a Spek
            that(AlsoSpek<Double, Double, Boolean>(ValueSpek(1.0), EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>(ValueSpek(2.0), EqualsSpek(2.2)).evaluate(2.2)).isFalse()
            that(AlsoSpek<Double, Double, Boolean>(ValueSpek(2.2), EqualsSpek(2.2)).evaluate(2.2)).isTrue()
        }
    }
}

fun expectCoroutine(block: suspend ExpectationBuilder.() -> Unit) = expect {
    runBlocking {
        block()
    }
}