/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 Nik De Kur
 */

package dev.nikdekur.ndkore.module

import dev.nikdekur.ndkore.interfaces.Snowflake

data class ModuleTask<A, M : Module<A>>(
    val moment: TaskMoment,
    override val id: String,
    val moduleClass : Class<M>,
    val task: (M) -> Unit
) : Snowflake<String>