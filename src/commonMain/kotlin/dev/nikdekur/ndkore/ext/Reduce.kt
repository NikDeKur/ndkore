package dev.nikdekur.ndkore.ext

/**
 * Performs a reduction operation on an array with an additional modifier applied to each element.
 *
 * Starting with the first element transformed by [modifier], applies [operation] to the accumulated value
 * and each following element (also transformed by [modifier]) to build the result.
 *
 * This function requires the array to have at least one element. For empty arrays, it will throw
 * [IndexOutOfBoundsException].
 *
 * Example usage:
 * ```
 * val array = arrayOf(1, 2, 3, 4)
 * val result = array.extendedReduce(
 *     modifier = { it * 2 },         // Double each value
 *     operation = { acc, value -> acc + value }  // Sum the doubled values
 * )
 * // Result: 20 (2 + 4 + 6 + 8)
 * ```
 *
 * @param S The type of the accumulated value, which may be a supertype of the array element type.
 * @param T The type of elements in the array.
 * @param modifier A function that transforms each element before it's used in the reduction.
 * @param operation A function that combines the current accumulated value and a transformed element.
 * @return The result of the reduction operation.
 * @throws IndexOutOfBoundsException if the array is empty.
 */
public inline fun <S, T : S> Array<out T>.extendedReduce(
    modifier: (T) -> T,
    operation: (S, T) -> S,
): S {
    var accumulator: S = modifier(this[0])
    for (index in 1..lastIndex) {
        accumulator = operation(accumulator, modifier(this[index]))
    }
    return accumulator
}

/**
 * Performs a reduction operation on an iterable collection with an additional modifier applied to each element.
 *
 * Starting with the first element transformed by [modifier], applies [operation] to the accumulated value
 * and each following element (also transformed by [modifier]) to build the result.
 *
 * This function requires the iterable to have at least one element. For empty iterables, it will throw
 * [NoSuchElementException].
 *
 * Example usage:
 * ```
 * val list = listOf(1, 2, 3, 4)
 * val result = list.extendedReduce(
 *     modifier = { it * 2 },         // Double each value
 *     operation = { acc, value -> acc + value }  // Sum the doubled values
 * )
 * // Result: 20 (2 + 4 + 6 + 8)
 * ```
 *
 * @param S The type of the accumulated value, which may be a supertype of the iterable element type.
 * @param T The type of elements in the iterable.
 * @param modifier A function that transforms each element before it's used in the reduction.
 * @param operation A function that combines the current accumulated value and a transformed element.
 * @return The result of the reduction operation.
 * @throws NoSuchElementException if the iterable is empty.
 */
public inline fun <S, T : S> Iterable<T>.extendedReduce(
    modifier: (T) -> T,
    operation: (S, T) -> S,
): S {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw NoSuchElementException("Collection is empty.")

    var accumulator: S = modifier(iterator.next())
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, modifier(iterator.next()))
    }
    return accumulator
}