package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.PGNGameTags

fun TestPGNGameDatabase(
    games: List<PGNGame> = testListOf(maxSize = 16) { TestPGNGame() }
): PGNGameDatabase {
    return PGNGameDatabase(games)
}