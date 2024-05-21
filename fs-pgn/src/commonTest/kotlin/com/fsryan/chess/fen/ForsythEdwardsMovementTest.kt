package com.fsryan.chess.fen

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNGamePiece.King
import com.fsryan.chess.pgn.PGNGamePiece.Pawn
import com.fsryan.chess.pgn.PGNGamePiece.Rook
import com.fsryan.chess.pgn.black
import com.fsryan.chess.pgn.sq
import com.fsryan.chess.pgn.white
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ForsythEdwardsMovementTest {

    @Test
    fun shouldCorrectlyMovePiece() {
        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION)
        val result = fen.movePiece(from = "e2".sq(), to = "e4".sq())
        val pieceAtE2 = result.pieceAt("e2".sq())
        val pieceAtE4 = result.pieceAt("e4".sq())
        assertNull(pieceAtE2)
        assertEquals(Pawn.white(), pieceAtE4)
        assertEquals("e3".sq(), result.enPassantTargetSquare)
        assertEquals(0, result.halfMoveClock)
        assertEquals(1, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertEquals("e3".sq(), fenValue.enPassantTargetSquare)
        assertEquals(0, fenValue.halfMoveClock)
        assertEquals(1, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldLoseCastlingRightsWhenMovingKingBlack() {
        val fen = ForsythEdwardsNotation("r3k2r/8/8/8/8/8/8/4K3 b kq - 0 1")
        val result = fen.movePiece(from = "e8".sq(), to = "e7".sq())
        assertFalse(result.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(result.blackHasCastlingRights(PGNCastle.QueenSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(2, result.fullMoveNumber)


        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.QueenSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(2, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldLoseCastlingRightsWhenMovingKingWhite() {
        val fen = ForsythEdwardsNotation("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val result = fen.movePiece(from = "e1".sq(), to = "e2".sq())
        assertFalse(result.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(result.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(1, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(1, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldCorrectlyCastleKingSideBlack() {
        val fen = ForsythEdwardsNotation("r3k2r/8/8/8/8/8/8/4K3 b kq - 0 1")
        val result = fen.performCastle(PGNCastle.KingSide)
        assertNull(result.pieceAt("e8".sq()))
        assertNull(result.pieceAt("h8".sq()))
        assertEquals(King.black(), result.pieceAt("g8".sq()))
        assertEquals(Rook.black(), result.pieceAt("f8".sq()))
        assertFalse(result.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(result.blackHasCastlingRights(PGNCastle.QueenSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(2, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.QueenSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(2, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldCorrectlyCastleKingSideWhite() {
        val fen = ForsythEdwardsNotation("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val result = fen.performCastle(PGNCastle.KingSide)
        assertNull(result.pieceAt("e1".sq()))
        assertNull(result.pieceAt("h1".sq()))
        assertEquals(King.white(), result.pieceAt("g1".sq()))
        assertEquals(Rook.white(), result.pieceAt("f1".sq()))
        assertFalse(result.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(result.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(1, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.KingSide))
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(1, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldCorrectlyCastleQueenSideBlack() {
        val fen = ForsythEdwardsNotation("r3k2r/8/8/8/8/8/8/4K3 b kq - 0 1")
        val result = fen.performCastle(PGNCastle.QueenSide)
        assertNull(result.pieceAt("e8".sq()))
        assertNull(result.pieceAt("a8".sq()))
        assertEquals(King.black(), result.pieceAt("c8".sq()))
        assertEquals(Rook.black(), result.pieceAt("d8".sq()))
        assertFalse(result.blackHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(result.blackHasCastlingRights(PGNCastle.KingSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(2, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fenValue.blackHasCastlingRights(PGNCastle.KingSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(2, fenValue.fullMoveNumber)
    }

    @Test
    fun shouldCorrectlyCastleQueenSideWhite() {
        val fen = ForsythEdwardsNotation("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val result = fen.performCastle(PGNCastle.QueenSide)
        assertNull(result.pieceAt("e1".sq()))
        assertNull(result.pieceAt("a1".sq()))
        assertEquals(King.white(), result.pieceAt("c1".sq()))
        assertEquals(Rook.white(), result.pieceAt("d1".sq()))
        assertFalse(result.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(result.whiteHasCastlingRights(PGNCastle.KingSide))
        assertNull(result.enPassantTargetSquare)
        assertEquals(1, result.halfMoveClock)
        assertEquals(1, result.fullMoveNumber)

        val fenValue = ForsythEdwardsNotation(result.serialValue)   // <-- after moving, we have a MapBasedFEN
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.QueenSide))
        assertFalse(fenValue.whiteHasCastlingRights(PGNCastle.KingSide))
        assertNull(fenValue.enPassantTargetSquare)
        assertEquals(1, fenValue.halfMoveClock)
        assertEquals(1, fenValue.fullMoveNumber)
    }
}