@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.ndkore.ext

import dev.nikdekur.ndkore.interfaces.Prioritizable
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Predicate


fun <T> newArrayList() = ArrayList<T>()
fun <T> newCopyOnWriteArrayList() = CopyOnWriteArrayList<T>()
fun <T> newHashSet() = HashSet<T>()
fun <T> newLinkedHashSet() = LinkedHashSet<T>()





@Suppress("UNCHECKED_CAST")
/**
 * Provides a way to cast a nullable array to a non-nullable array.
 * Unsafe to use if the array contains nulls.
 *
 * @return the array casted to non-nullable
 */
inline fun <T> Array<T?>.notNull(): Array<T> {
    return this as Array<T>
}

/**
 * Creating a new array from the collection.
 *
 * Set the type of array as 'out T'
 *
 * Function is created to allow an array store covariant type
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

inline fun <reified T> Collection<Collection<T>>.toTArray(): Array<Array<out T>> {
    val main = arrayOfNulls<Array<out T>>(size)
    this.forEachIndexed { index, item ->
        main[index] = item.toTArray()
    }
    @Suppress("UNCHECKED_CAST")
    return main as Array<Array<out T>>
}

/**
 * @return true if the item was added
 */
inline fun <T> MutableCollection<T>.addIfNotContains(item: T): Boolean {
    val willBeAdded = !contains(item)
    if (willBeAdded)
        add(item)
    return willBeAdded
}


@Suppress("UNCHECKED_CAST")
inline fun <T, R> Collection<T>.cast(clazz: Class<R>): Collection<R> = this as Collection<R>



inline fun <T> List<T>.optimize(): List<T> = when (size) {
    0 -> emptyList()
    1 -> listOf(this[0])
    else -> this
}

inline fun <T> MutableCollection<T>.removeIfNot(predicate: Predicate<T>): Boolean {
    return removeIf(predicate.negate())
}

inline fun <T> MutableCollection<T>.removeIfAssignable(fromClass: Class<out T>): Boolean {
    return removeIf { fromClass.isInstance(it) }
}
inline fun <T> MutableCollection<T>.removeIfNotAssignable(fromClass: Class<out T>): Boolean {
    return removeIf { !fromClass.isInstance(it) }
}


inline fun <T> T.toSingletonSet() = setOf(this)
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
 * A method to synchronize a collection, which is a thread-safe version of the collection.
 * Synchronization is done by wrapping the collection with a synchronized wrapper.
 * Synchoronized collections are thread-safe, but they are not efficient for concurrent access.
 * If you need to perform a lot of operations on the collection, consider using a concurrent collection.
 * 
 * @return A synchronized copy of the collection
 */
inline fun <T> MutableCollection<T>.synchronized(): MutableCollection<T> {
    return Collections.synchronizedCollection(this)
}

/**
 * A method to synchronize a set, which is a thread-safe version of the set.
 * Synchronization is done by wrapping the set with a synchronized wrapper.
 * Synchoronized collections are thread-safe, but they are not efficient for concurrent access.
 * If you need to perform a lot of operations on the collection, consider using a concurrent collection.
 *
 * @return A synchronized copy of the set
 */
inline fun <T> MutableSet<T>.synchronized(): MutableSet<T> {
    return Collections.synchronizedSet(this)
}

/**
 * A method to synchronize a list, which is a thread-safe version of the list.
 * Synchronization is done by wrapping the list with a synchronized wrapper.
 * Synchoronized collections are thread-safe, but they are not efficient for concurrent access.
 * If you need to perform a lot of operations on the collection, consider using a concurrent collection.
 *
 * @return A synchronized copy of the list
 */
inline fun <T> MutableList<T>.synchronized(): MutableList<T> {
    return Collections.synchronizedList(this)
}

/**
 * A method to synchronize a map, which is a thread-safe version of the map.
 * Synchronization is done by wrapping the map with a synchronized wrapper.
 * Synchoronized collections are thread-safe, but they are not efficient for concurrent access.
 * If you need to perform a lot of operations on the collection, consider using a concurrent collection.
 *
 * @return A synchronized copy of the map
 */
inline fun <K, V> MutableMap<K, V>.synchronized(): MutableMap<K, V> {
    return Collections.synchronizedMap(this)
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


inline fun <T : MutableCollection<String>> T.copyPartialMatches(token: String, from: Iterable<String>): T {
    from
        .filter { it.startsWith(token, ignoreCase = true) }
        .forEach { e: String -> add(e) }
    return this
}

inline fun <T : MutableCollection<String>> T.filterPartialMatches(token: String) {
    removeIf {
        !it.startsWith(token, ignoreCase = true)
    }
}



inline fun <T> MutableCollection<T>.addAll(vararg elements: T) = addAll(elements)


fun <T> List<T>.random(random: Random, amount: Int): List<T> {
    check(amount >= 0) { "amount cannot be less than 0" }
    if (amount == 0) return emptyList()

    val list = ArrayList<T>(amount)
    for (i in 1..amount) {
        val index = random.randInt(0, lastIndex)
        list.add(this[index])
    }
    return list
}

fun <T> Collection<T>.randomUnique(random: Random, amount: Int): Collection<T> {
    check(amount >= 0) { "amount cannot be less than 0" }
    if (amount == 0) return emptyList()
    if (amount == size) return ArrayList(this)

    val list = HashSet<T>(amount)
    while (true) {
        val index = random.randInt(0, size + 1)
        list.addIfNotContains(this.elementAt(index))
        if (list.size == amount) break
    }

    return list
}
