@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn.test

import com.benasher44.uuid.uuid4
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.random.Random

/**
 * Returns a random optional value, with a given null rate.
 *
 * @param nullRate the rate of null values
 * @param produce the function to produce the value
 *
 * @return the value, or null
 */
@JsExport
fun <T:Any> randomOptional(nullRate: Float = 0.5F, produce: () -> T?): T? {
    return if (Random.nextFloat() >= nullRate) produce() else null
}

/**
 * Returns a random email address
 *
 * @return the email address
 */
@JsExport
fun randomEmail(): String {
    return "example+${randomUUIDString()}@example.com"
}

@JsExport
fun randomHttpsUrl(): String {
    return "https://example.com/${randomUUIDString()}"
}

/**
 * Returns a random UUID string
 *
 * @return the UUID string
 */
@JsExport
fun randomUUIDString(): String = uuid4().toString()

/**
 * Returns a random optional UUID, with a given null rate.
 *
 * @param nullRate the rate of null values
 *
 * @return the UUID, or null
 */
@JsExport
fun randomOptionalUUID(nullRate: Float = 0.5F): String? = randomOptional(nullRate, ::randomUUIDString)

/**
 * Returns a random enum value of the required type. The [predicate] function
 * enables filtering of the possible values.
 *
 * @param T the enum type
 * @param predicate the predicate function
 *
 * @return the random enum value
 */
inline fun <reified T : Enum<T>> randomEnumValue(predicate: (T) -> Boolean = { true }): T {
    return enumValues<T>().filter(predicate).random()
}