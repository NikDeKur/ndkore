package dev.nikdekur.ndkore.module

import dev.nikdekur.ndkore.interfaces.Snowflake

data class ModuleTask<A, M : Module<A>>(
    val moment: TaskMoment,
    override val id: String,
    val moduleClass : Class<M>,
    val task: (M) -> Unit
) : Snowflake<String>