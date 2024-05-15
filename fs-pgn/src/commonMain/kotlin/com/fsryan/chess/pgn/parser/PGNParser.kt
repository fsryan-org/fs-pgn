package com.fsryan.chess.pgn.parser

import okio.BufferedSource

fun interface PGNParser<out E: Any> {
    fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<E>
}