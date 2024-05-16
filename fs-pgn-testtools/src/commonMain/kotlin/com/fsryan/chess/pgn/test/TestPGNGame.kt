package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNGameTags

fun TestPGNGame(
    gameTags: PGNGameTags = TestPGNGameTags(),
    moves: List<PGNGamePly> = testListOf(maxSize = 24) { plyIndex ->
        val isBlack = plyIndex % 2 == 1
        val moveNumber = plyIndex / 2 + 1
        TestPGNGamePly(
            moveNumber = moveNumber,
            isBlack = isBlack,
            numberIndicator = if (isBlack) null else moveNumber,
            sanMove = TestPGNSANMove(isBlack = isBlack, suffixAnnotation = null)    // <-- suffix annotations are deserialized, but not serialized--they are converted to NAG
        )
    },
    result: PGNGameResult? = randomOptional { TestPGNGameResult(comments = emptyList()) }
): PGNGame {
    val elements = result?.let { moves.plus(it) } ?: moves
    return PGNGame(tags = gameTags, elements = elements)
}