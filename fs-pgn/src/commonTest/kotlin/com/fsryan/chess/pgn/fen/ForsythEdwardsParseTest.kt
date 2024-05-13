package com.fsryan.chess.pgn.fen

import com.fsryan.chess.fen.FEN_STANDARD_STARTING_POSITION
import com.fsryan.chess.fen.ForsythEdwardsNotation
import com.fsryan.chess.fen.blackIsActive
import com.fsryan.chess.fen.whiteIsActive
import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.PlayerGamePiece
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ForsythEdwardsParseTest {

    @Test
    fun shouldCorrectlyParseStandardFEN() {

        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION)

        assertNull(fen.enPassantTargetSquare)
        assertEquals(1, fen.fullMoveNumber)
        assertEquals(0, fen.halfMoveClock)
        assertEquals('w'.code, fen.activePlayerCharacterCode)
        assertTrue(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.blackHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fen.blackIsActive())
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertTrue(fen.whiteIsActive())

        // assert white piece locations
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.King), fen.pieceAt(PGNSquare("e1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h1")))

        // assert empty locations
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(3, 4, 5, 6).forEach { rank ->
                assertNull(fen.pieceAt(PGNSquare(file = file, rank = rank)))
            }
        }

        // assert black piece locations
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.King), fen.pieceAt(PGNSquare("e8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h8")))
    }

    @Test
    fun shouldCorrectlyParseFENAfter1e4() {
        val input = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"

        val fen = ForsythEdwardsNotation(input)

        assertEquals(PGNSquare(file = 'e', rank = 3), fen.enPassantTargetSquare)
        assertEquals(1, fen.fullMoveNumber)
        assertEquals(0, fen.halfMoveClock)
        assertEquals('b'.code, fen.activePlayerCharacterCode)
        assertTrue(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.blackHasCastlingRights(PGNCastle.QueenSide))
        assertTrue(fen.blackIsActive())
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fen.whiteIsActive())

        // assert white piece locations
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e4")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.King), fen.pieceAt(PGNSquare("e1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h1")))

        // assert empty locations
        assertEquals(null, fen.pieceAt(PGNSquare("e2")))
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(3, 4, 5, 6).filter { rank ->
                !(file == 'e' && rank == 4)
            }.forEach { rank ->
                assertNull(fen.pieceAt(PGNSquare(file = file, rank = rank)))
            }
        }

        // assert black piece locations
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.King), fen.pieceAt(PGNSquare("e8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h8")))
    }

    @Test
    fun shouldCorrectlyParseFENAfter1e4c5() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertEquals(PGNSquare(file = 'c', rank = 6), fen.enPassantTargetSquare)
        assertEquals(2, fen.fullMoveNumber)
        assertEquals(0, fen.halfMoveClock)
        assertEquals('w'.code, fen.activePlayerCharacterCode)
        assertTrue(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.blackHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fen.blackIsActive())
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertTrue(fen.whiteIsActive())

        // assert white piece locations
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e4")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.King), fen.pieceAt(PGNSquare("e1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h1")))

        // assert empty locations
        assertEquals(null, fen.pieceAt(PGNSquare("e2")))
        assertEquals(null, fen.pieceAt(PGNSquare("c7")))
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(3, 4, 5, 6).filter { rank ->
                !(file == 'e' && rank == 4) && !(file == 'c' && rank == 5)
            }.forEach { rank ->
                assertNull(fen.pieceAt(PGNSquare(file = file, rank = rank)))
            }
        }

        // assert black piece locations
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c5")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.King), fen.pieceAt(PGNSquare("e8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h8")))
    }

    @Test
    fun shouldCorrectlyParseFENAfter1e4c5Nf3() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertNull(fen.enPassantTargetSquare)
        assertEquals(2, fen.fullMoveNumber)
        assertEquals(0, fen.halfMoveClock)
        assertEquals('b'.code, fen.activePlayerCharacterCode)
        assertTrue(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.blackHasCastlingRights(PGNCastle.QueenSide))
        assertTrue(fen.blackIsActive())
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fen.whiteIsActive())


        // assert white piece locations
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e4")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h2")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.King), fen.pieceAt(PGNSquare("e1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f1")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("f3")))
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h1")))

        // assert empty locations
        assertEquals(null, fen.pieceAt(PGNSquare("e2")))
        assertEquals(null, fen.pieceAt(PGNSquare("c7")))
        assertEquals(null, fen.pieceAt(PGNSquare("g1")))
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(3, 4, 5, 6).filter { rank ->
                !(file == 'e' && rank == 4) && !(file == 'c' && rank == 5) && !(file == 'f' && rank == 3)
            }.forEach { rank ->
                assertNull(fen.pieceAt(PGNSquare(file = file, rank = rank)))
            }
        }

        // assert black piece locations
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("a7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("b7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("c5")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("d7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("e7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("f7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("g7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Pawn), fen.pieceAt(PGNSquare("h7")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("a8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("b8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("c8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Queen), fen.pieceAt(PGNSquare("d8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.King), fen.pieceAt(PGNSquare("e8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Bishop), fen.pieceAt(PGNSquare("f8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Knight), fen.pieceAt(PGNSquare("g8")))
        assertEquals(PlayerGamePiece(isBlack = true, PGNGamePiece.Rook), fen.pieceAt(PGNSquare("h8")))
    }

    @Test
    fun shouldCorrectlyUnderstandBlackDoesNotHaveKingSideCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQq - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertFalse(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.blackHasCastlingRights(PGNCastle.QueenSide))
    }

    @Test
    fun shouldCorrectlyUnderstandBlackDoesNotHaveQueenSideCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQk - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertTrue(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fen.blackHasCastlingRights(PGNCastle.QueenSide))
    }

    @Test
    fun shouldCorrectlyUnderstandBlackDoesNotHaveCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQ - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertFalse(fen.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fen.blackHasCastlingRights(PGNCastle.QueenSide))
    }

    @Test
    fun shouldCorrectlyUnderstandWhiteDoesNotHaveKingSideCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w Qkq - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertFalse(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertTrue(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
    }

    @Test
    fun shouldCorrectlyUnderstandWhiteDoesNotHaveQueenSideCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w Kkq - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertTrue(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
    }

    @Test
    fun shouldCorrectlyUnderstandWhiteDoesNotHaveCastlingRights() {
        val input = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w kq - 0 2"

        val fen = ForsythEdwardsNotation(input)

        assertFalse(fen.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fen.whiteHasCastlingRights(PGNCastle.QueenSide))
    }
}