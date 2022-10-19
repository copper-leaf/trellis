package com.copperleaf.trellis.impl

import com.copperleaf.trellis.base.CandidateSpek
import com.copperleaf.trellis.base.ValueSpek
import com.copperleaf.trellis.visitor.EmptyVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TestDates : StringSpec({

    "testBetweenDates" {
        val startDate = LocalDate.now().minusDays(5)
        val endDate = LocalDate.now().plusDays(5)

        val spek = BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        spek.evaluate(EmptyVisitor, LocalDate.now()) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10)) shouldBe false
        spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10)) shouldBe false

        spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(5)) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(5)) shouldBe true
    }

    "testNoEndDate" {
        val startDate = LocalDate.now().minusDays(5)
        val endDate: LocalDate? = null

        val spek =
            BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        spek.evaluate(EmptyVisitor, LocalDate.now()) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10)) shouldBe false
        spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10)) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.MAX) shouldBe true

        spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(5)) shouldBe true
    }

    "testNoStartDate" {
        val startDate: LocalDate? = null
        val endDate = LocalDate.now().plusDays(5)

        val spek =
            BetweenDatesSpek(ValueSpek(startDate), ValueSpek(endDate), CandidateSpek())

        spek.evaluate(EmptyVisitor, LocalDate.now()) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.now().minusDays(10)) shouldBe true
        spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(10)) shouldBe false
        spek.evaluate(EmptyVisitor, LocalDate.MIN) shouldBe true

        spek.evaluate(EmptyVisitor, LocalDate.now().plusDays(5)) shouldBe true
    }
})
