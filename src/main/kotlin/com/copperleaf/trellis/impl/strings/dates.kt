package com.copperleaf.trellis.impl.strings

import com.copperleaf.trellis.api.Spek
import java.time.LocalDate

class BetweenDatesSpek<T>(
        private val startDate: Spek<T, LocalDate?>,
        private val endDate: Spek<T, LocalDate?>,
        private val targetDate: Spek<T, LocalDate>
) : Spek<T, Boolean> {

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