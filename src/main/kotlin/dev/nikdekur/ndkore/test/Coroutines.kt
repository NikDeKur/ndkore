/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

suspend inline fun realDelay(timeMillis: Long, dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    @Suppress("kotlin:S6311")
    withContext(dispatcher) {
        delay(timeMillis)
    }
}