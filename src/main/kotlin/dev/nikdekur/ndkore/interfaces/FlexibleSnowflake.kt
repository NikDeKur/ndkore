package dev.nikdekur.ndkore.interfaces

interface FlexibleSnowflake<T> : Snowflake<T> {
    override var id: T
}
