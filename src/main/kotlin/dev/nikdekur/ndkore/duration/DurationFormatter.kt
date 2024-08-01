/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.duration

import dev.nikdekur.ndkore.ext.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.DurationUnit

class DurationFormatter {



    object RU : Formatter {
        override fun format(duration: Duration): String {
            val years = duration.toInt(DurationUnit.DAYS) / 365
            val months = duration.toInt(DurationUnit.DAYS) % 365 / 30
            val weeks = duration.toInt(DurationUnit.DAYS) % 365 % 30 / 7
            val days = duration.toInt(DurationUnit.DAYS) % 365 % 30 % 7
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val result = StringBuilder()

            if (years > 0)
                result.append(formatUnit(years, "год", "года", "лет")).append(" ")
            if (months > 0)
                result.append(formatUnit(months, "месяц", "месяца", "месяцев")).append(" ")
            if (weeks > 0)
                result.append(formatUnit(weeks, "неделя", "недели", "недель")).append(" ")
            if (days > 0)
                result.append(formatUnit(days, "день", "дня", "дней")).append(" ")
            if (hours > 0)
                result.append(formatUnit(hours, "час", "часа", "часов")).append(" ")
            if (minutes > 0)
                result.append(formatUnit(minutes, "минута", "минуты", "минут")).append(" ")
            if (seconds > 0 || result.isEmpty())
                result.append(formatUnit(seconds, "секунда", "секунды", "секунд")).append(" ")

            return result.toString().trim { it <= ' ' }
        }

        private fun formatUnit(value: Number, form1: String, form2: String, form5: String): String {
            return if (value % 10 == 1.0 && value % 100 != 11.0) {
                "$value $form1"
            } else if (value % 10 in 2.0..4.0 && (value % 100 < 10 || value % 100 >= 20)) {
                "$value $form2"
            } else {
                "$value $form5"
            }
        }
    }











    object EN : Formatter {
        override fun format(duration: Duration): String {
            val years = duration.toInt(DurationUnit.DAYS) / 365
            val months = duration.toInt(DurationUnit.DAYS) % 365 / 30
            val weeks = duration.toInt(DurationUnit.DAYS) % 365 % 30 / 7
            val days = duration.toInt(DurationUnit.DAYS) % 365 % 30 % 7
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()

            val result = StringBuilder()

            if (years > 0)
                result.append(formatUnit(years, "year", "years")).append(" ")
            if (months > 0)
                result.append(formatUnit(months, "month", "months")).append(" ")
            if (weeks > 0)
                result.append(formatUnit(weeks, "week", "weeks")).append(" ")
            if (days > 0)
                result.append(formatUnit(days, "day", "days")).append(" ")
            if (hours > 0)
                result.append(formatUnit(hours, "hour", "hours")).append(" ")
            if (minutes > 0)
                result.append(formatUnit(minutes, "minute", "minutes")).append(" ")
            if (seconds > 0 || result.isEmpty())
                result.append(formatUnit(seconds, "second", "seconds")).append(" ")

            return result.trim().toString()
        }

        private fun formatUnit(value: Number, form1: String, form5: String): String {
            return when {
                (value % 10).toLong() == 1L && (value % 100).toLong() != 11L -> "$value $form1"
                value % 10 in 2.0..4.0 && (value % 100 < 10 || value % 100 >= 20) -> "$value $form1"
                else -> "$value $form5"
            }
        }
    }


    object UA : Formatter {
        override fun format(duration: Duration): String {
            val years = duration.toInt(DurationUnit.DAYS) / 365
            val months = duration.toInt(DurationUnit.DAYS) % 365 / 30
            val weeks = duration.toInt(DurationUnit.DAYS) % 365 % 30 / 7
            val days = duration.toInt(DurationUnit.DAYS) % 365 % 30 % 7
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()

            val result = StringBuilder()

            if (years > 0)
                result.append(formatUnit(years, "рік", "року", "років")).append(" ")
            if (months > 0)
                result.append(formatUnit(months, "місяць", "місяця", "місяців")).append(" ")
            if (weeks > 0)
                result.append(formatUnit(weeks, "тиждень", "тижня", "тижнів")).append(" ")
            if (days > 0)
                result.append(formatUnit(days, "день", "дня", "днів")).append(" ")
            if (hours > 0)
                result.append(formatUnit(hours, "година", "години", "годин")).append(" ")
            if (minutes > 0)
                result.append(formatUnit(minutes, "хвилина", "хвилини", "хвилин")).append(" ")
            if (seconds > 0 || result.isEmpty())
                result.append(formatUnit(seconds, "секунда", "секунди", "секунд")).append(" ")

            return result.toString().trim()
        }

        private fun formatUnit(value: Number, form1: String, form2: String, form5: String): String {
            return if (value % 10 == 1.0 && value % 100 != 11.0) {
                "$value $form1"
            } else if (value % 10 in 2.0..4.0 && (value % 100 < 10 || value % 100 >= 20)) {
                "$value $form2"
            } else {
                "$value $form5"
            }
        }
    }

    object DE : Formatter {
        override fun format(duration: Duration): String {
            val years = duration.toInt(DurationUnit.DAYS) / 365
            val months = duration.toInt(DurationUnit.DAYS) % 365 / 30
            val weeks = duration.toInt(DurationUnit.DAYS) % 365 % 30 / 7
            val days = duration.toInt(DurationUnit.DAYS) % 365 % 30 % 7
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val millis = duration.toMillisPart()


            val result = StringBuilder()

            if (years > 0)
                result.append(formatUnit(years, "Jahr", "Jahre")).append(" ")
            if (months > 0)
                result.append(formatUnit(months, "Monat", "Monate")).append(" ")
            if (weeks > 0)
                result.append(formatUnit(weeks, "Woche", "Wochen")).append(" ")
            if (days > 0)
                result.append(formatUnit(days, "Tag", "Tage")).append(" ")
            if (hours > 0)
                result.append(formatUnit(hours, "Stunde", "Stunden")).append(" ")
            if (minutes > 0)
                result.append(formatUnit(minutes, "Minute", "Minuten")).append(" ")
            if (seconds > 0)
                result.append(formatUnit(seconds, "Sekunde", "Sekunden")).append(" ")
            if (millis > 0)
                result.append(formatUnit(millis, "Millisekunde", "Millisekunden")).append(" ")
            if (result.isEmpty())
                result.append("0 Sekunden")


            return result.trim().toString()
        }

        private fun formatUnit(value: Number, form1: String, form5: String): String {
            return when {
                (value % 10).toLong() == 1L && (value % 100).toLong() != 11L -> "$value $form1"
                value % 10 in 2.0..4.0 && (value % 100 < 10 || value % 100 >= 20) -> "$value $form1"
                else -> "$value $form5"
            }
        }
    }



    companion object {


        // TODO: Add Map for languages and remove slow reflect-search
        private val extraFormatters: ConcurrentHashMap<String, Formatter> = ConcurrentHashMap()

        @JvmStatic
        fun addExtraFormatter(language: String, formatter: Formatter) {
            extraFormatters[language] = formatter
        }

        @JvmStatic
        fun format(duration: Duration, language: String): String {
            try {
                val formatterClass = getNestedClass<DurationFormatter>(language.uppercase())
                return (formatterClass
                    .getInstanceField()
                    .r_CallMethod("format", duration))
                    .value as String

            } catch (_: ClassNotFoundException) {
                val formatter = extraFormatters[language]
                if (formatter != null) {
                    return formatter.format(duration)
                }

                throw UnsupportedLanguageException(language)
            } catch (e: Exception) {
                throw e
            }
        }
    }



    class UnsupportedLanguageException(language: String) : RuntimeException("Support doesn't exists for '$language'")

    fun interface Formatter {
        fun format(duration: Duration): String
    }


    data class Configuration(
        val allowMillis: Boolean = false,
        val allowSeconds: Boolean = true,
        val allowMinutes: Boolean = true,
        val allowHours: Boolean = true,
        val allowDays: Boolean = true,
        val allowWeeks: Boolean = true,
        val allowMonths: Boolean = true,
        val allowYears: Boolean = true
    )
}