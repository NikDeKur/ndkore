package dev.nikdekur.ndkore.ext

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

public class LocalDateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    private val stepDays: Long = 1
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> =
        LocalDateIterator(start, endInclusive, stepDays)

    public infix fun step(days: Long): LocalDateRange = LocalDateRange(start, endInclusive, days)
}

public operator fun LocalDate.rangeTo(other: LocalDate): LocalDateRange = LocalDateRange(this, other)

public class LocalDateIterator(
    startDate: LocalDate,
    private val endDate: LocalDate,
    private val stepDays: Long
) : Iterator<LocalDate> {
    private var currentDate = startDate

    override fun hasNext(): Boolean = currentDate <= endDate

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plus(stepDays, DateTimeUnit.DAY)
        return next
    }
}


public inline val LocalDate.firstDayOfWeek: LocalDate
    get() = minus(this.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)

public inline val LocalDate.lastDayOfWeek: LocalDate
    get() = plus((6 - this.dayOfWeek.ordinal).toLong(), DateTimeUnit.DAY)