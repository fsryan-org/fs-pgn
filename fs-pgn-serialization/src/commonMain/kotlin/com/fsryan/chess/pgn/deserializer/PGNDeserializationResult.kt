package com.fsryan.chess.pgn.deserializer

/**
 * The result of a PGN Finite State Machine completing its work. This result
 * includes the number of characters read and the value that was read.
 */
interface PGNDeserializationResult<out T: Any> {
    val charactersRead: Int
    val value: T
}

fun <T:Any> PGNDeserializationResult(charactersRead: Int, value: T): PGNDeserializationResult<T> {
    return PGNDeserializationResultData(charactersRead, value)
}

private data class PGNDeserializationResultData<out T: Any>(
    override val charactersRead: Int,
    override val value: T
): PGNDeserializationResult<T>