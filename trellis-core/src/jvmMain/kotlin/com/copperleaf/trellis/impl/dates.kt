@file:Suppress("NewApi")
package com.copperleaf.trellis.impl

import com.copperleaf.trellis.api.Spek
import com.copperleaf.trellis.introspection.visitor.SpekVisitor
import com.copperleaf.trellis.introspection.visitor.visiting
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

    override val children = listOf(startDate, endDate, targetDate)

    override fun evaluate(visitor: SpekVisitor, candidate: T): Boolean {
        return visiting(visitor) {
            val startDateValue: LocalDate? = startDate.evaluate(visitor, candidate)
            val endDateValue: LocalDate? = endDate.evaluate(visitor, candidate)
            val targetDateValue: LocalDate = targetDate.evaluate(visitor, candidate)

            val passesStartDate = if (startDateValue != null)
                startDateValue.isBefore(targetDateValue) || startDateValue == targetDateValue
            else
                true

            val passesEndDate = if (endDateValue != null)
                endDateValue.isAfter(targetDateValue) || endDateValue == targetDateValue
            else
                true

            passesStartDate && passesEndDate
        }
    }
}

class DateSpek<T>(
    private val year: Spek<T, Int>,
    private val month: Spek<T, Int>,
    private val day: Spek<T, Int>
) : Spek<T, LocalDate> {

    override val children = listOf(year, month, day)

    override fun evaluate(visitor: SpekVisitor, candidate: T): LocalDate {
        return visiting(visitor) {
            val yearValue = year.evaluate(visitor, candidate)
            val monthValue = month.evaluate(visitor, candidate)
            val dayValue = day.evaluate(visitor, candidate)

            LocalDate.of(yearValue, monthValue, dayValue)
        }
    }
}
