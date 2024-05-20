package com.fsryan.chess.pgn

import com.fsryan.chess.pgn.test.allFiles
import com.fsryan.chess.pgn.test.allRanks
import kotlin.test.Test
import kotlin.test.assertNull

// TODO: move this to fs-pgn
class PGNSquareNextSquareTest {

    @Test
    fun shouldReturnNullWhenPreviousFileIsOffTheBoard() {
        allRanks().forEach { rank ->
            assertNull(PGNSquare('a', rank).previousFile())
        }
    }

    @Test
    fun shouldReturnNullWhenNextFileIsOffTheBoard() {
        allRanks().forEach { rank ->
            assertNull(PGNSquare('h', rank).nextFile())
        }
    }

    @Test
    fun shouldReturnNullWhenPreviousRankIsOffTheBoard() {
        allFiles().forEach { file ->
            assertNull(PGNSquare(file, 1).previousRank())
        }
    }

    @Test
    fun shouldReturnNullWhenNextRankIsOffTheBoard() {
        allFiles().forEach { file ->
            assertNull(PGNSquare(file, 8).nextRank())
        }
    }

    @Test
    fun shouldReturnNullWhenNextDiagonalIsOffTheBoard() {
        allRanks().forEach { rank ->
            assertNull(PGNSquare('a', rank).nextDiagonal(left = true, up = true))
            assertNull(PGNSquare('a', rank).nextDiagonal(left = true, up = false))
            assertNull(PGNSquare('h', rank).nextDiagonal(left = false, up = true))
            assertNull(PGNSquare('h', rank).nextDiagonal(left = false, up = false))
        }
        allFiles().forEach { file ->
            assertNull(PGNSquare(file, 1).nextDiagonal(left = true, up = false))
            assertNull(PGNSquare(file, 1).nextDiagonal(left = false, up = false))
            assertNull(PGNSquare(file, 8).nextDiagonal(left = true, up = true))
            assertNull(PGNSquare(file, 8).nextDiagonal(left = false, up = true))
        }
    }
}