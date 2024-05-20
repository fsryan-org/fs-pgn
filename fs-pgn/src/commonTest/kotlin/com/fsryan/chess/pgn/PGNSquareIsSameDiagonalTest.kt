package com.fsryan.chess.pgn

import com.fsryan.chess.pgn.test.allFiles
import com.fsryan.chess.pgn.test.allRanks
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: move this to fs-pgn?
class PGNSquareIsSameDiagonalTest {

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA1() {
        val receiver = PGNSquare('a', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 2),
                PGNSquare('c', 3),
                PGNSquare('d', 4),
                PGNSquare('e', 5),
                PGNSquare('f', 6),
                PGNSquare('g', 7),
                PGNSquare('h', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA2() {
        val receiver = PGNSquare('a', 2)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 1),
                PGNSquare('b', 3),
                PGNSquare('c', 4),
                PGNSquare('d', 5),
                PGNSquare('e', 6),
                PGNSquare('f', 7),
                PGNSquare('g', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA3() {
        val receiver = PGNSquare('a', 3)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 2),
                PGNSquare('c', 1),
                PGNSquare('b', 4),
                PGNSquare('c', 5),
                PGNSquare('d', 6),
                PGNSquare('e', 7),
                PGNSquare('f', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA4() {
        val receiver = PGNSquare('a', 4)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 3),
                PGNSquare('c', 2),
                PGNSquare('d', 1),
                PGNSquare('b', 5),
                PGNSquare('c', 6),
                PGNSquare('d', 7),
                PGNSquare('e', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA5() {
        val receiver = PGNSquare('a', 5)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 4),
                PGNSquare('c', 3),
                PGNSquare('d', 2),
                PGNSquare('e', 1),
                PGNSquare('b', 6),
                PGNSquare('c', 7),
                PGNSquare('d', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA6() {
        val receiver = PGNSquare('a', 6)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 5),
                PGNSquare('c', 4),
                PGNSquare('d', 3),
                PGNSquare('e', 2),
                PGNSquare('f', 1),
                PGNSquare('b', 7),
                PGNSquare('c', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA7() {
        val receiver = PGNSquare('a', 7)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 6),
                PGNSquare('c', 5),
                PGNSquare('d', 4),
                PGNSquare('e', 3),
                PGNSquare('f', 2),
                PGNSquare('g', 1),
                PGNSquare('b', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalA8() {
        val receiver = PGNSquare('a', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 7),
                PGNSquare('c', 6),
                PGNSquare('d', 5),
                PGNSquare('e', 4),
                PGNSquare('f', 3),
                PGNSquare('g', 2),
                PGNSquare('h', 1)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalB8() {
        val receiver = PGNSquare('b', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('a', 7),
                PGNSquare('c', 7),
                PGNSquare('d', 6),
                PGNSquare('e', 5),
                PGNSquare('f', 4),
                PGNSquare('g', 3),
                PGNSquare('h', 2)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalC8() {
        val receiver = PGNSquare('c', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('b', 7),
                PGNSquare('a', 6),
                PGNSquare('d', 7),
                PGNSquare('e', 6),
                PGNSquare('f', 5),
                PGNSquare('g', 4),
                PGNSquare('h', 3)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalD8() {
        val receiver = PGNSquare('d', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('c', 7),
                PGNSquare('b', 6),
                PGNSquare('a', 5),
                PGNSquare('e', 7),
                PGNSquare('f', 6),
                PGNSquare('g', 5),
                PGNSquare('h', 4)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalE8() {
        val receiver = PGNSquare('e', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('d', 7),
                PGNSquare('c', 6),
                PGNSquare('b', 5),
                PGNSquare('a', 4),
                PGNSquare('f', 7),
                PGNSquare('g', 6),
                PGNSquare('h', 5)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalF8() {
        val receiver = PGNSquare('f', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('e', 7),
                PGNSquare('d', 6),
                PGNSquare('c', 5),
                PGNSquare('b', 4),
                PGNSquare('a', 3),
                PGNSquare('g', 7),
                PGNSquare('h', 6)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalG8() {
        val receiver = PGNSquare('g', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('f', 7),
                PGNSquare('e', 6),
                PGNSquare('d', 5),
                PGNSquare('c', 4),
                PGNSquare('b', 3),
                PGNSquare('a', 2),
                PGNSquare('h', 7)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH8() {
        val receiver = PGNSquare('h', 8)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 7),
                PGNSquare('f', 6),
                PGNSquare('e', 5),
                PGNSquare('d', 4),
                PGNSquare('c', 3),
                PGNSquare('b', 2),
                PGNSquare('a', 1)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH7() {
        val receiver = PGNSquare('h', 7)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 6),
                PGNSquare('f', 5),
                PGNSquare('e', 4),
                PGNSquare('d', 3),
                PGNSquare('c', 2),
                PGNSquare('b', 1),
                PGNSquare('g', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH6() {
        val receiver = PGNSquare('h', 6)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 5),
                PGNSquare('f', 4),
                PGNSquare('e', 3),
                PGNSquare('d', 2),
                PGNSquare('c', 1),
                PGNSquare('g', 7),
                PGNSquare('f', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH5() {
        val receiver = PGNSquare('h', 5)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 4),
                PGNSquare('f', 3),
                PGNSquare('e', 2),
                PGNSquare('d', 1),
                PGNSquare('g', 6),
                PGNSquare('f', 7),
                PGNSquare('e', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH4() {
        val receiver = PGNSquare('h', 4)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 3),
                PGNSquare('f', 2),
                PGNSquare('e', 1),
                PGNSquare('g', 5),
                PGNSquare('f', 6),
                PGNSquare('e', 7),
                PGNSquare('d', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH3() {
        val receiver = PGNSquare('h', 3)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 2),
                PGNSquare('f', 1),
                PGNSquare('g', 4),
                PGNSquare('f', 5),
                PGNSquare('e', 6),
                PGNSquare('d', 7),
                PGNSquare('c', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH2() {
        val receiver = PGNSquare('h', 2)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 1),
                PGNSquare('g', 3),
                PGNSquare('f', 4),
                PGNSquare('e', 5),
                PGNSquare('d', 6),
                PGNSquare('c', 7),
                PGNSquare('b', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalH1() {
        val receiver = PGNSquare('h', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 2),
                PGNSquare('f', 3),
                PGNSquare('e', 4),
                PGNSquare('d', 5),
                PGNSquare('c', 6),
                PGNSquare('b', 7),
                PGNSquare('a', 8)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalG1() {
        val receiver = PGNSquare('g', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('h', 2),
                PGNSquare('f', 2),
                PGNSquare('e', 3),
                PGNSquare('d', 4),
                PGNSquare('c', 5),
                PGNSquare('b', 6),
                PGNSquare('a', 7)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalF1() {
        val receiver = PGNSquare('f', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('g', 2),
                PGNSquare('h', 3),
                PGNSquare('e', 2),
                PGNSquare('d', 3),
                PGNSquare('c', 4),
                PGNSquare('b', 5),
                PGNSquare('a', 6)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalE1() {
        val receiver = PGNSquare('e', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('f', 2),
                PGNSquare('g', 3),
                PGNSquare('h', 4),
                PGNSquare('d', 2),
                PGNSquare('c', 3),
                PGNSquare('b', 4),
                PGNSquare('a', 5)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalD1() {
        val receiver = PGNSquare('d', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('e', 2),
                PGNSquare('f', 3),
                PGNSquare('g', 4),
                PGNSquare('h', 5),
                PGNSquare('c', 2),
                PGNSquare('b', 3),
                PGNSquare('a', 4)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalC1() {
        val receiver = PGNSquare('c', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('d', 2),
                PGNSquare('e', 3),
                PGNSquare('f', 4),
                PGNSquare('g', 5),
                PGNSquare('h', 6),
                PGNSquare('b', 2),
                PGNSquare('a', 3)
            )
        )
    }

    @Test
    fun shouldCorrectlyDetermineWhetherOnSameDiagonalB1() {
        val receiver = PGNSquare('b', 1)
        runAllDiagonalsExpectations(
            receiver = receiver,
            expectedDiagonals = setOf(
                receiver,
                PGNSquare('c', 2),
                PGNSquare('d', 3),
                PGNSquare('e', 4),
                PGNSquare('f', 5),
                PGNSquare('g', 6),
                PGNSquare('h', 7),
                PGNSquare('a', 2)
            )
        )
    }

    private fun runAllDiagonalsExpectations(receiver: PGNSquare, expectedDiagonals: Set<PGNSquare>) {
        allFiles().flatMap { file ->
            allRanks().map { rank ->
                PGNSquare(file, rank)
            }
        }.forEach { square ->
            val expected = expectedDiagonals.contains(square)
            val actual = receiver.isOnSameDiagonal(square)
            assertEquals(
                expected = expected,
                actual = actual,
                message = "expected $receiver to${if (expected) "" else " not"} be on the same diagonal as $square"
            )
        }
    }
}