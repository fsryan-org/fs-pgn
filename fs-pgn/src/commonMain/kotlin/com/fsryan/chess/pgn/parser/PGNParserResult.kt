package com.fsryan.chess.pgn.parser

/**
 * The result of a PGN Finite State Machine completing its work. This result
 * includes the number of characters read and the value that was read.
 */
interface PGNParserResult<out T: Any> {
    val charactersRead: Int
    val value: T
}

fun <T:Any> PGNFSMResult(charactersRead: Int, value: T): PGNParserResult<T> = PGNParserResultData(charactersRead, value)

private data class PGNParserResultData<out T: Any>(
    override val charactersRead: Int,
    override val value: T
): PGNParserResult<T>