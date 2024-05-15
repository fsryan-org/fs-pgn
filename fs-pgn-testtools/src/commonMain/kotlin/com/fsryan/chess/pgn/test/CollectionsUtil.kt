package com.fsryan.chess.pgn.test

import kotlin.random.Random

/**
 * Returns a test list of a random size between [minSize] and [maxSize], using
 * the [produce] function to produce the elements.
 *
 * @param minSize the minimum size of the list
 * @param maxSize the maximum size of the list
 * @param produce the function to produce the elements
 *
 * @return the list
 */
fun <T:Any?> testListOf(minSize: Int = 0, maxSize: Int, produce: (index: Int) -> T): List<T> {
    val actualMinSize = minSize.coerceIn(0, Int.MAX_VALUE - 1)
    val actualMaxSize = maxSize.coerceIn(actualMinSize, Int.MAX_VALUE - 1)
    val ret = mutableListOf<T>()
    val size = Random.nextInt(actualMinSize, actualMaxSize + 1)
    for (i in 0 until size) {
        ret.add(produce(i))
    }
    return ret
}

/**
 * Returns a map of a random size between [minSize] and [maxSize] with a given
 * set of possible keys ([possibleKeys]), using the [produceValue] function to
 *
 * @param possibleKeys the set of possible keys
 * @param minSize the minimum size of the map
 * @param maxSize the maximum size of the map
 * @param produceValue the function to produce the values
 *
 * @return the map
 */
fun <K: Any, V: Any> testMapOf(
    possibleKeys: Set<K>,
    minSize: Int = 0,
    maxSize: Int = possibleKeys.size,
    produceValue: (k: K) -> V
): Map<K, V> {
    val actualMinSize = minSize.coerceIn(0, possibleKeys.size)
    val actualMaxSize = maxSize.coerceIn(actualMinSize, possibleKeys.size)
    val ret = mutableMapOf<K, V>()
    val size = Random.nextInt(actualMinSize, actualMaxSize + 1)
    val remainingKeys = possibleKeys.toMutableSet()
    while (remainingKeys.isNotEmpty() && ret.size <= size) {
        val key = remainingKeys.random()
        remainingKeys.remove(key)
        ret[key] = produceValue(key)
    }
    return ret.toMap()
}