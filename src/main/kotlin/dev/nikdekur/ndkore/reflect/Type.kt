/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.reflect

import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

inline fun KType(
    classifier: KClassifier,
    isNullable: Boolean = false,
    annotations: List<Annotation> = emptyList(),
    arguments: List<KTypeProjection> = emptyList(),
) = object : KType {
    override val classifier = classifier
    override val isMarkedNullable = isNullable
    override val arguments = arguments
    override val annotations = annotations
}