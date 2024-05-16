package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNMoveSectionElement
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.pgnString
import com.fsryan.chess.pgn.test.TestPGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.test.TestSimplePGNSANMove
import com.fsryan.chess.pgn.test.TestSimplePly
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNMoveTextSerializerTest {

    @Test
    fun shouldCorrectlySerializeEmptyMoveText() {
        val input = emptyList<PGNMoveSectionElement>()
        val expected = ""

        val actual = StringBuilder().addPGNMoveSectionElements(input).toString()

        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithOnlyResult() {
        PGNGameResultValue.entries.forEach { resultValue ->
            val input: List<PGNMoveSectionElement> = listOf(PGNGameResult(value = resultValue, comments = emptyList()))
            val expected = resultValue.serialValue

            val actual = StringBuilder().addPGNMoveSectionElements(input).toString()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithOnlyOneWhiteMove() {
        val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val expected = "1. ${ply1.sanMove.pgnString}"

        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1)).toString()

        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithOnlyOneWhiteAndBlackMove() {
        val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val ply2 = TestSimplePly(isBlack = true, numberIndicator = 1)
        val expected = "1. ${ply1.sanMove.pgnString} ${ply2.sanMove.pgnString}"

        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1, ply2)).toString()

        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithOnlyTwoWhiteAndOneBlackMove() {
        val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val ply2 = TestSimplePly(isBlack = true, numberIndicator = null)
        val ply3 = TestSimplePly(isBlack = false, numberIndicator = 2)
        val expected = "1. ${ply1.sanMove.pgnString} ${ply2.sanMove.pgnString} 2. ${ply3.sanMove.pgnString}"

        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1, ply2, ply3)).toString()

        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithNumericAnnotationGlyph() {
        PGNNumericAnnotationGlyph.entries
            .filter { it != PGNNumericAnnotationGlyph.Unknown }
            .forEachIndexed { index, nag ->
                val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1, numericAnnotationGlyph = nag)
                val expected = "1. ${ply1.sanMove.pgnString} ${nag.pgnString}"
                val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1)).toString()
                assertEquals(expected, actual)
            }
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithComments() {
        val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1, comments = listOf("comment1", "comment2"))
        val expected = "1. ${ply1.sanMove.pgnString} {comment1} {comment2}"
        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1)).toString()
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithRecursiveAnnotationVariation() {
        val ply1RecursiveMove1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val ply1 = TestSimplePly(
            isBlack = false,
            numberIndicator = 1,
            recursiveAnnotationVariation = TestPGNRecursiveVariationAnnotation(
                firstMoveIsBlack = false,
                firstMoveNumber = 1,
                plies = listOf(ply1RecursiveMove1)
            )
        )
        val expected = "1. ${ply1.sanMove.pgnString} (1. ${ply1RecursiveMove1.sanMove.pgnString})"
        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1)).toString()
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithRecursiveAnnotationVariationWhenStartingFromBlackMove() {
        val ply2RecursiveMove1 = TestSimplePly(isBlack = true, numberIndicator = 1)
        val ply1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val ply2 = TestSimplePly(
            isBlack = true,
            numberIndicator = null,
            recursiveAnnotationVariation = TestPGNRecursiveVariationAnnotation(
                firstMoveNumber = 1,
                firstMoveIsBlack = true,
                listOf(ply2RecursiveMove1)
            )
        )
        val expected = "1. ${ply1.sanMove.pgnString} ${ply2.sanMove.pgnString} (1... ${ply2RecursiveMove1.sanMove.pgnString})"
        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1, ply2)).toString()
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlySerializeMoveTextWithDepth2RecursiveAnnotationVariation() {
        val ply1RecursiveMove1RecursiveMove1 = TestSimplePly(isBlack = false, numberIndicator = 1)
        val ply1RecursiveMove1RecursiveMove2 = TestSimplePly(isBlack = true, numberIndicator = null)
        val ply1RecursiveMove1 = TestSimplePly(
            isBlack = false,
            numberIndicator = 1,
            recursiveAnnotationVariation = TestPGNRecursiveVariationAnnotation(
                firstMoveNumber = 1,
                firstMoveIsBlack = false,
                listOf(ply1RecursiveMove1RecursiveMove1, ply1RecursiveMove1RecursiveMove2)
            )
        )
        val ply1RecursiveMove2 = TestSimplePly(isBlack = true, numberIndicator = null)
        val ply1 = TestSimplePly(
            isBlack = false,
            numberIndicator = 1,
            recursiveAnnotationVariation = TestPGNRecursiveVariationAnnotation(
                firstMoveNumber = 1,
                firstMoveIsBlack = false,
                listOf(ply1RecursiveMove1, ply1RecursiveMove2)
            )
        )
        val expected = "1. ${ply1.sanMove.pgnString} (1. ${ply1RecursiveMove1.sanMove.pgnString} (1. ${ply1RecursiveMove1RecursiveMove1.sanMove.pgnString} ${ply1RecursiveMove1RecursiveMove2.sanMove.pgnString}) ${ply1RecursiveMove2.sanMove.pgnString})"
        val actual = StringBuilder().addPGNMoveSectionElements(listOf(ply1)).toString()
        assertEquals(expected, actual)
    }

    @Test
    fun shouldWrapAt80Characters() {
        val plies = (0 until 15).map { index ->
            TestSimplePly(
                isBlack = index % 2 == 1,
                numberIndicator = if (index % 2 == 1) null else index / 2 + 1,
                sanMove = TestSimplePGNSANMove(
                    piece = PGNGamePiece.Pawn,
                    destination = PGNSquare(
                        file = 'a' + index % 8,
                        rank = when (index % 2 == 0) {
                            true -> if (index / 8 == 0) 3 else 4
                            false -> if (index / 8 == 0) 6 else 5
                        }
                    )
                )
            )
        }.plus(TestSimplePly(isBlack = false, numberIndicator = 17, sanMove = TestSimplePGNSANMove(piece = PGNGamePiece.Rook, destination = PGNSquare(file = 'a', rank = 2))))
        .plus(TestSimplePly(isBlack = true, numberIndicator = null, sanMove = TestSimplePGNSANMove(piece = PGNGamePiece.Rook, destination = PGNSquare(file = 'a', rank = 7))))
        .plus(TestSimplePly(isBlack = false, numberIndicator = 18, sanMove = TestSimplePGNSANMove(piece = PGNGamePiece.Rook, destination = PGNSquare(file = 'h', rank = 2))))
        .plus(TestSimplePly(isBlack = true, numberIndicator = null, sanMove = TestSimplePGNSANMove(piece = PGNGamePiece.Rook, destination = PGNSquare(file = 'h', rank = 7))))
        val expected = "1. a3 b6 2. c3 d6 3. e3 f6 4. g3 h6 5. a4 b5 6. c4 d5 7. e4 f5 8. g4 8. Ra2 Ra7\n9. Rh2 Rh7"

        val actual = StringBuilder().addPGNMoveSectionElements(plies).toString()
        assertEquals(expected, actual)
    }
}