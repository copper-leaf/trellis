package com.copperleaf.trellis.api

import com.copperleaf.trellis.impl.strings.BetweenDatesSpek
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.time.LocalDate

class TestDates {

    @Test
    fun testBetweenDates() = runBlocking<Unit> {
        val startDate = LocalDate.now().minusDays(5)
        val endDate = LocalDate.now().plusDays(5)

        val spek = BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now())).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10))).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10))).isFalse()

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(5))).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(5))).isTrue()
    }

    @Test
    fun testNoEndDate() = runBlocking<Unit> {
        val startDate = LocalDate.now().minusDays(5)
        val endDate: LocalDate? = null

        val spek = BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now())).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10))).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10))).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.MAX)).isTrue()

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(5))).isTrue()
    }

    @Test
    fun testNoStartDate() = runBlocking<Unit> {
        val startDate: LocalDate? = null
        val endDate = LocalDate.now().plusDays(5)

        val spek = BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now())).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10))).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10))).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.MIN)).isTrue()

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(5))).isTrue()
    }



}
