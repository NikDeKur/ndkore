/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

package dev.nikdekur.ndkore.spatial.kdtree

import dev.nikdekur.ndkore.spatial.Point

public data class Node<T>(
    var point: Point,
    var value: T,
    var left: Node<T>? = null,
    var right: Node<T>? = null,
)