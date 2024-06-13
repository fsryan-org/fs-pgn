package com.fsryan.chess.pgn

import kotlin.test.Test
import kotlin.test.assertEquals

class PGNSquareTest {

    @Test
    fun shouldCorrectlyEnumeratePositionWithLowerCaseFiles() {
        sequenceOf(
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
        ).forEachIndexed { index, positionStr ->
            val square = PGNSquare(positionStr)
            val expectedFile = positionStr[0]
            assertEquals(expectedFile, square.file)
            assertEquals(index / 8 + 1, square.rank)
        }
    }

    @Test
    fun shouldCorrectlyEnumeratePositionWithUpperCaseFiles() {
        sequenceOf(
            "A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1",
            "A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2",
            "A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3",
            "A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4",
            "A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5",
            "A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6",
            "A7", "B7", "C7", "D7", "E7", "F7", "G7", "H7",
            "A8", "B8", "C8", "D8", "E8", "F8", "G8", "H8",
        ).forEachIndexed { index, positionStr ->
            val position = PGNSquare(positionStr)
            val expectedFile = positionStr[0].lowercaseChar()
            assertEquals(expectedFile, position.file)
            assertEquals(index / 8 + 1, position.rank)
        }
    }

    @Test
    fun shouldCorrectlyCalculateFileIndices() {
        sequenceOf(
            "a8" to 0, "b8" to 1, "c8" to 2, "d8" to 3, "e8" to 4, "f8" to 5, "g8" to 6, "h8" to 7,
            "a7" to 0, "b7" to 1, "c7" to 2, "d7" to 3, "e7" to 4, "f7" to 5, "g7" to 6, "h7" to 7,
            "a6" to 0, "b6" to 1, "c6" to 2, "d6" to 3, "e6" to 4, "f6" to 5, "g6" to 6, "h6" to 7,
            "a5" to 0, "b5" to 1, "c5" to 2, "d5" to 3, "e5" to 4, "f5" to 5, "g5" to 6, "h5" to 7,
            "a4" to 0, "b4" to 1, "c4" to 2, "d4" to 3, "e4" to 4, "f4" to 5, "g4" to 6, "h4" to 7,
            "a3" to 0, "b3" to 1, "c3" to 2, "d3" to 3, "e3" to 4, "f3" to 5, "g3" to 6, "h3" to 7,
            "a2" to 0, "b2" to 1, "c2" to 2, "d2" to 3, "e2" to 4, "f2" to 5, "g2" to 6, "h2" to 7,
            "a1" to 0, "b1" to 1, "c1" to 2, "d1" to 3, "e1" to 4, "f1" to 5, "g1" to 6, "h1" to 7,
        ).forEachIndexed { index, (positionStr, expectedFileIndex) ->
            val square = positionStr.sq()
            assertEquals(expectedFileIndex, square.fileIdx())
        }
    }

    @Test
    fun shouldCorrectlyCalculateRankIndices() {
        sequenceOf(
            "a8" to 7, "b8" to 7, "c8" to 7, "d8" to 7, "e8" to 7, "f8" to 7, "g8" to 7, "h8" to 7,
            "a7" to 6, "b7" to 6, "c7" to 6, "d7" to 6, "e7" to 6, "f7" to 6, "g7" to 6, "h7" to 6,
            "a6" to 5, "b6" to 5, "c6" to 5, "d6" to 5, "e6" to 5, "f6" to 5, "g6" to 5, "h6" to 5,
            "a5" to 4, "b5" to 4, "c5" to 4, "d5" to 4, "e5" to 4, "f5" to 4, "g5" to 4, "h5" to 4,
            "a4" to 3, "b4" to 3, "c4" to 3, "d4" to 3, "e4" to 3, "f4" to 3, "g4" to 3, "h4" to 3,
            "a3" to 2, "b3" to 2, "c3" to 2, "d3" to 2, "e3" to 2, "f3" to 2, "g3" to 2, "h3" to 2,
            "a2" to 1, "b2" to 1, "c2" to 1, "d2" to 1, "e2" to 1, "f2" to 1, "g2" to 1, "h2" to 1,
            "a1" to 0, "b1" to 0, "c1" to 0, "d1" to 0, "e1" to 0, "f1" to 0, "g1" to 0, "h1" to 0,
        ).forEachIndexed { index, (positionStr, expectedRankIndex) ->
            val square = positionStr.sq()
            assertEquals(expectedRankIndex, square.rankIdx())
        }
    }

    @Test
    fun shouldCorrectlyCalculateIsLight() {
        sequenceOf(
            "a8" to true, "b8" to false, "c8" to true, "d8" to false, "e8" to true, "f8" to false, "g8" to true, "h8" to false,
            "a7" to false, "b7" to true, "c7" to false, "d7" to true, "e7" to false, "f7" to true, "g7" to false, "h7" to true,
            "a6" to true, "b6" to false, "c6" to true, "d6" to false, "e6" to true, "f6" to false, "g6" to true, "h6" to false,
            "a5" to false, "b5" to true, "c5" to false, "d5" to true, "e5" to false, "f5" to true, "g5" to false, "h5" to true,
            "a4" to true, "b4" to false, "c4" to true, "d4" to false, "e4" to true, "f4" to false, "g4" to true, "h4" to false,
            "a3" to false, "b3" to true, "c3" to false, "d3" to true, "e3" to false, "f3" to true, "g3" to false, "h3" to true,
            "a2" to true, "b2" to false, "c2" to true, "d2" to false, "e2" to true, "f2" to false, "g2" to true, "h2" to false,
            "a1" to false, "b1" to true, "c1" to false, "d1" to true, "e1" to false, "f1" to true, "g1" to false, "h1" to true,
        ).forEach { (positionStr, expectedLight) ->
            val square = positionStr.sq()
            assertEquals(expectedLight, square.isLight())
        }
    }

    @Test
    fun shouldCorrectlyCalculateIsDark() {
        sequenceOf(
            "a8" to false, "b8" to true, "c8" to false, "d8" to true, "e8" to false, "f8" to true, "g8" to false, "h8" to true,
            "a7" to true, "b7" to false, "c7" to true, "d7" to false, "e7" to true, "f7" to false, "g7" to true, "h7" to false,
            "a6" to false, "b6" to true, "c6" to false, "d6" to true, "e6" to false, "f6" to true, "g6" to false, "h6" to true,
            "a5" to true, "b5" to false, "c5" to true, "d5" to false, "e5" to true, "f5" to false, "g5" to true, "h5" to false,
            "a4" to false, "b4" to true, "c4" to false, "d4" to true, "e4" to false, "f4" to true, "g4" to false, "h4" to true,
            "a3" to true, "b3" to false, "c3" to true, "d3" to false, "e3" to true, "f3" to false, "g3" to true, "h3" to false,
            "a2" to false, "b2" to true, "c2" to false, "d2" to true, "e2" to false, "f2" to true, "g2" to false, "h2" to true,
            "a1" to true, "b1" to false, "c1" to true, "d1" to false, "e1" to true, "f1" to false, "g1" to true, "h1" to false,
        ).forEach { (positionStr, expectedLight) ->
            val square = positionStr.sq()
            assertEquals(expectedLight, square.isDark())
        }
    }
}