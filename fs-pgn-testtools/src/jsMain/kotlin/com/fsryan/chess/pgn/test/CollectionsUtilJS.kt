@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn.test

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
@JsExport
fun <T:Any?> testArrayOf(minSize: Int = 0, maxSize: Int, produce: (index: Int) -> T): Array<T> {
    return testListOf(minSize, maxSize, produce).toTypedArray()
}