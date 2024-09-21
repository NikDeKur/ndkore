/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024-present "Nik De Kur"
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.`interface`.Prioritizable
import kotlin.random.Random
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
/**
 * Cast a nullable array to a non-nullable array.
 *
 * Unsafe to use if the array contains nulls.
 *
 * @return the array casted to non-nullable
 */
inline fun <T> Array<T?>.notNull(): Array<T> {
    return this as Array<T>
}

/**
 * Create a new array from the collection.
 *
 * Set the type of array as 'out T'
 *
 * Function is created to allow an array store covariant type
 *
 * @return the array created from the collection
 */
inline fun <reified T> Collection<T>.toTArray(): Array<out T> {
    val array = arrayOfNulls<T>(size)
    this.forEachIndexed { index, item ->
        array[index] = item
    }
    @Suppress("UNCHECKED_CAST")
    return array as Array<out T>
}

/**
 * Create a new array from the collection of collections.
 *
 * Set the type of array as 'out T'
 *
 * Function is created to allow an array store covariant type
 *
 * @return the array created from the collection of collections
 */
inline fun <reified T> Collection<Collection<T>>.toTArray(): Array<Array<out T>> {
    val main = arrayOfNulls<Array<out T>>(size)
    this.forEachIndexed { index, item ->
        main[index] = item.toTArray()
    }
    @Suppress("UNCHECKED_CAST")
    return main as Array<Array<out T>>
}

/**
 * Add an item to the collection if the item is not already in the collection.
 *
 * Not recommended to use with [MutableSet], using [MutableSet.add] would be more efficient.
 *
 * @return true if the item was added
 */
inline fun <T> MutableCollection<T>.addIfNotContains(item: T): Boolean {
    val willBeAdded = !contains(item)
    if (willBeAdded)
        add(item)
    return willBeAdded
}


@Suppress("UNCHECKED_CAST")
/**
 * Cast a collection to a collection of different types.
 *
 * Unsafe to use if the collection contains elements of a different type.
 *
 * @return the collection casted to a different type
 */
inline fun <T, R> Collection<T>.cast(): Collection<R> = this as Collection<R>


/**
 * A method to try to optimize a list.
 *
 * If the list is empty, the method will return an [emptyList]
 *
 * If the list has only one element, the method will return a [Collections.SingletonList]
 *
 * If the list has more than one element, the method will return the list itself without any changes.
 *
 * @return An optimized list
 */
inline fun <T> List<T>.optimize(): List<T> = when (size) {
    0 -> emptyList()
    1 -> listOf(this[0])
    else -> this
}

inline fun <T> MutableCollection<T>.removeIf(predicate: (T) -> Boolean): Boolean {
    val iterator = iterator()
    var modified = false
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            iterator.remove()
            modified = true
        }
    }
    return modified
}

/**
 * Removes all elements from this mutable collection that do not match the given predicate.
 *
 * @param predicate a predicate which returns `true` for elements to be removed.
 * @return `true` if any elements were removed from this collection, `false` otherwise.
 */
inline fun <T> MutableCollection<T>.removeIfNot(crossinline predicate: (T) -> Boolean): Boolean {
    return removeIf { !predicate(it) }
}

/**
 * Removes all elements from this mutable collection that are instances of the specified class.
 *
 * @param fromClass the class of the elements to be removed.
 * @return `true` if any elements were removed from this collection, `false` otherwise.
 */
inline fun <T : Any> MutableCollection<T>.removeIfAssignable(fromClass: KClass<out T>): Boolean {
    return removeIf { fromClass.isInstance(it) }
}

/**
 * Removes all elements from this mutable collection that are not instances of the specified class.
 *
 * @param fromClass the class of the elements to be retained.
 * @return `true` if any elements were removed from this collection, `false` otherwise.
 */
inline fun <T : Any> MutableCollection<T>.removeIfNotAssignable(fromClass: KClass<out T>): Boolean {
    return removeIf { !fromClass.isInstance(it) }
}

/**
 * Returns a singleton set containing only this element.
 *
 * @return a set containing only this element.
 */
inline fun <T> T.toSingletonSet() = setOf(this)

/**
 * Returns a singleton list containing only this element.
 *
 * @return a list containing only this element.
 */
inline fun <T> T.toSingletonList() = listOf(this)



/**
 * A method to sort a list of [Prioritizable] objects by their priority.
 *
 * The method will sort the list in descending order. Objects with higher [Prioritizable.priority] will be placed first.
 *
 * @return A sorted list (copy) of [Prioritizable] objects
 * @see Prioritizable
 * @see sortedByDescending
 */
inline fun <T> List<T>.sortByPriority(): List<T> where T : Prioritizable {
    return sortedByDescending { it.priority }
}


/**
 * A safe method to get a sublist of a list.
 *
 * If the fromIndexInclusive is less than 0, the method will return a sublist of the list from 0.
 *
 * If the toIndexExclusive is greater than the size of the list, the method will return a sublist of the list from the start index to the end of the list.
 *
 * If the indexes are out of bounds, the method will return a sublist of the list from the start index to the end of the list.
 *
 * @param fromIndexInclusive The start index of the sublist
 * @param toIndexExclusive The end index of the sublist
 * @return A sublist of the list
 */
inline fun <T> List<T>.sub(fromIndexInclusive: Int, toIndexExclusive: Int): List<T> {
    val start = if (fromIndexInclusive < 0) 0 else fromIndexInclusive
    if (size < toIndexExclusive) return subList(0, size)
    return subList(start, toIndexExclusive)
}


/**
 * A method to iterate over a list and perform an action on each element with an option to add or remove elements.
 *
 * Method use indexes to get access to the next element and stop iteration if no element at the next index exists.
 *
 * The recommended List implementation to use with this method is ArrayList.
 *
 * @param action The action to perform on each element
 */
fun <T> List<T>.mutableForEach(action: (T) -> Unit) {
    var index = 0

    while (index < size) {
        val element = try {
            this[index]
        } catch (e: IndexOutOfBoundsException) { break }
        action(element)
        index++
    }
}

/**
 * A method to iterate over a list and perform an action on each element with an option to add or remove elements.
 *
 * Method use indexes to get access to the next element and stop iteration if no element at the next index exists.
 *
 * The recommended List implementation to use with this method is ArrayList.
 *
 * @param action The action to perform on each element
 */
fun <T> List<T>.mutableForEachIndexed(action: (Int, T) -> Unit) {
    var index = 0

    while (index < size) {
        val element = try {
            this[index]
        } catch (e: IndexOutOfBoundsException) { break }
        action(index, element)
        index++
    }
}

/**
 * A method to iterate over a list and perform an action on each element with an option to add or remove elements.
 *
 * Method use indexes to get access to the next element and stop iteration if no element at the next index exists.
 *
 * The recommended List implementation to use with this method is ArrayList.
 *
 * Method will catch any exception thrown by the action and call the onError method with the element and the exception.
 *
 * @param onError The method to call if the action throws an exception
 * @param action The action to perform on each element
 */
fun <T> List<T>.mutableSafeForEach(onError: (T, Exception) -> Unit, action: (T) -> Unit) {
    var index = 0

    while (index < size) {
        val element = try {
            this[index]
        } catch (e: Exception) { break }
        try {
            action(element)
        } catch (e: Exception) {
            onError(element, e)
        }
        index++
    }
}


/**
 * Copies all elements from the given iterable that start with the specified token (case-insensitive)
 * into this mutable collection.
 *
 * @param token the token to match at the beginning of each element.
 * @param from the iterable containing elements to be matched and copied.
 * @return this mutable collection with the matched elements added.
 */
inline fun <T : MutableCollection<String>> T.copyPartialMatches(token: String, from: Iterable<String>): T {
    from
        .filter { it.startsWith(token, ignoreCase = true) }
        .forEach { e: String -> add(e) }
    return this
}

/**
 * Removes all elements from this mutable collection that do not start with the specified token (case-insensitive).
 *
 * @param token the token to match at the beginning of each element.
 */
inline fun <T : MutableCollection<String>> T.filterPartialMatches(token: String) {
    removeIf {
        !it.startsWith(token, ignoreCase = true)
    }
}

/**
 * Adds all the specified elements to this mutable collection.
 *
 * @param elements the elements to add to this collection.
 * @return `true` if the collection was modified as a result of the operation, `false` otherwise.
 */
inline fun <T> MutableCollection<T>.addAll(vararg elements: T) = addAll(elements)

/**
 * Returns a list containing a specified number of random elements from this list.
 *
 * @param random the random number generator to use for selecting random elements.
 * @param amount the number of random elements to select.
 * @return a list containing the specified number of random elements.
 * @throws IllegalArgumentException if the amount is less than 0.
 */
fun <T> List<T>.random(random: Random, amount: Int): List<T> {
    require(amount >= 0) { "amount cannot be less than 0" }
    if (amount == 0) return emptyList()

    val list = ArrayList<T>(amount)
    repeat(amount) {
        val index = random.nextInt(lastIndex + 1)
        list.add(this[index])
    }
    return list
}


/**
 * An optimised variant of [listOfNotNull].
 *
 * The method will create a new list and add all elements that are not null.
 *
 * @param elements The elements to add to the list
 * @return A list of non-null elements
 * @see listOfNotNull
 */
inline fun <T> notNullListOf(vararg elements: T?): List<T> {
    val list = mutableListOf<T>()
    elements.forEach { if (it != null) list.add(it) }
    return list
}

/**
 * Adds all elements that are not null to the list.
 *
 * Does not create a new list, but adds elements to the existing list.
 *
 * @param elements The elements to add to the list
 */
inline fun <T> MutableList<T>.addAllNotNull(vararg elements: T?) {
    elements.forEach { if (it != null) add(it) }
}


/**
 * Find the first element in iterable that is an instance of the specified class.
 *
 * @param T the type of the class
 * @return the first element that is an instance of the specified class
 * @throws NoSuchElementException if no such element is found
 */
inline fun <reified T> Iterable<*>.firstInstance() = first { it is T } as T

/**
 * Find the first element in iterable that is an instance of the specified class or null if no such element is found.
 *
 * @param T the type of the class
 * @return the first element that is an instance of the specified class or null if no such element is found
 * @throws NoSuchElementException if no such element is found
 */
inline fun <reified T> Iterable<*>.firstInstanceOrNull() = firstOrNull { it is T } as? T