package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.api.ValueSpek
import java.time.LocalDate

class BetweenDatesSpek<T>(
        private val startDate: Spek<T, LocalDate?>,
        private val endDate: Spek<T, LocalDate?>,
        private val targetDate: Spek<T, LocalDate>
) : Spek<T, Boolean> {

    constructor(
        startDate: LocalDate?,
        endDate: LocalDate?,
        targetDate: LocalDate
    ) : this(ValueSpek(startDate), ValueSpek(endDate), ValueSpek(targetDate))

    override suspend fun evaluate(candidate: T): Boolean {
        val startDateValue  = startDate.evaluate(candidate)
        val endDateValue    = endDate.evaluate(candidate)
        val targetDateValue = targetDate.evaluate(candidate)

        val passesStartDate = if(startDateValue != null) {
            startDateValue.isBefore(targetDateValue) || startDateValue  == targetDateValue
        }
        else {
            true
        }
        val passesEndDate = if(endDateValue != null) {
            endDateValue.isAfter(targetDateValue) || endDateValue  == targetDateValue
        }
        else {
            true
        }

        return passesStartDate && passesEndDate
    }
}

class DateSpek<T>(
    private val year: Spek<T, Int>,
    private val month: Spek<T, Int>,
    private val day: Spek<T, Int>
) : Spek<T, LocalDate> {

    override suspend fun evaluate(candidate: T): LocalDate {
        val yearValue = year.evaluate(candidate)
        val monthValue = month.evaluate(candidate)
        val dayValue = day.evaluate(candidate)

        return LocalDate.of(yearValue, monthValue, dayValue)
    }
}