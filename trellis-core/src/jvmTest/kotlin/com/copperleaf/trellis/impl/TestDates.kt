package com.copperleaf.trellis.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.expectThat
import com.copperleaf.trellis.visitor.EmptyVisitor
import com.copperleaf.trellis.isFalse
import com.copperleaf.trellis.isTrue
import java.time.LocalDate
import kotlin.test.Test

class TestDates {

    @Test
    fun testBetweenDates() {
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
    fun testNoEndDate() {
        val startDate = LocalDate.now().minusDays(5)
        val endDate: LocalDate? = null

        val spek =
            BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now())).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10))).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10))).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.MAX)).isTrue()

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(5))).isTrue()
    }

    @Test
    fun testNoStartDate() {
        val startDate: LocalDate? = null
        val endDate = LocalDate.now().plusDays(5)

        val spek =
            BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now())).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10))).isTrue()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10))).isFalse()
        expectThat(spek.evaluate(EmptyVisitor, LocalDate.MIN)).isTrue()

        expectThat(spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(5))).isTrue()
    }
}
