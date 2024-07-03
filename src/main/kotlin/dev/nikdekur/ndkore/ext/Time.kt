/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.duration.DurationFormatter
import java.time.Duration
import java.util.*

inline fun Duration.toHoursPart(): Int {
    return (toHours() % 24).toInt()
}
inline fun Duration.toMinutesPart(): Int {
    return (toMinutes() % 60).toInt()
}

inline fun Duration.toSecondsPart(): Int {
    return (seconds % 60).toInt()
}

inline fun Duration.toMillisPart(): Int {
    return (toMillis() % 1_000).toInt()
}

inline fun Duration.toNanosPart(): Int {
    return (toNanos() % 1_000_000_000).toInt()
}

inline fun Duration.toReadableString(language: String): String {
    return DurationFormatter.format(this, language)
}

inline fun Duration.toReadableString(locale: Locale): String {
    return toReadableString(locale.language)
}