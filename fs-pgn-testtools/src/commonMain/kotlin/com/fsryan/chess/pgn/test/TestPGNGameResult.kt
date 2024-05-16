package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNGameResultValue

fun TestPGNGameResult(
    comments: List<String> = testListOf(maxSize = 3) { randomUUIDString() },
    value: PGNGameResultValue = randomEnumValue()
): PGNGameResult {
    return PGNGameResult(value, comments = comments)
}