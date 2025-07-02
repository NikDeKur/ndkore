package dev.nikdekur.ndkore.reflect

public interface ReflectMethod {

    public val supportMethodCalling: Boolean

    /**
     * Finds a value at a path in an object.
     *
     * The reason to return [NotFound] instead of simple [null] is because
     * value at the path might really be null.
     *
     * @param obj The object to find the value in.
     * @param name The path to find the value at.
     * @return The value at the path in the object or [NotFound] if no value found at the path
     */
    public fun findValue(obj: Any, name: String): Any?

    /**
     * An object indicating that no result found on a path.
     */
    public data object NotFound
}


