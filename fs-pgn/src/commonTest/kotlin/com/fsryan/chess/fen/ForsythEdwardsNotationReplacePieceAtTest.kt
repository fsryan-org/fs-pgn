package com.fsryan.chess.fen

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNGamePiece.King
import com.fsryan.chess.pgn.PGNGamePiece.Pawn
import com.fsryan.chess.pgn.PGNGamePiece.Rook
import com.fsryan.chess.pgn.sq
import com.fsryan.chess.pgn.white
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ForsythEdwardsNotationReplacePieceAtTest {

    @Test
    fun shouldCorrectlyMovePiece() {
        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION)
        val result = fen.movePiece(from = "e2".sq(), to = "e4".sq())
        val pieceAtE2 = result.pieceAt("e2".sq())
        val pieceAtE4 = result.pieceAt("e4".sq())
        assertNull(pieceAtE2)
        assertEquals(Pawn.white(), pieceAtE4)
    }

    @Test
    fun shouldCorrectlyCastleKingSide() {
        val fen = ForsythEdwardsNotation("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val result = fen.performCastle(PGNCastle.KingSide)
        assertNull(result.pieceAt("e1".sq()))
        assertNull(result.pieceAt("h1".sq()))
        assertEquals(King.white(), result.pieceAt("g1".sq()))
        assertEquals(Rook.white(), result.pieceAt("f1".sq()))
    }

    @Test
    fun shouldCorrectlyCastleQueenSide() {
        val fen = ForsythEdwardsNotation("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val result = fen.performCastle(PGNCastle.QueenSide)
        assertNull(result.pieceAt("e1".sq()))
        assertNull(result.pieceAt("a1".sq()))
        assertEquals(King.white(), result.pieceAt("c1".sq()))
        assertEquals(Rook.white(), result.pieceAt("d1".sq()))
    }
}