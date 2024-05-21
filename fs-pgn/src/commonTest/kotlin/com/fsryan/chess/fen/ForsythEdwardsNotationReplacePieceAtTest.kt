package com.fsryan.chess.fen

import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.PlayerGamePiece
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ForsythEdwardsNotationReplacePieceAtTest {

    @Test
    fun shouldCorrectlyRemovePiece() {
        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION)
        val result = fen.replacePieceAt(PGNSquare("e2"), null)
        val pieceAtE2 = result.pieceAt(PGNSquare("e2"))
        assertNull(pieceAtE2)
    }

    @Test
    fun shouldCorrectlyReplacePiece() {
        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION)
        val result = fen.replacePieceAt(PGNSquare("e2"))
            .replacePieceAt(PGNSquare("e4"), PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn))
        val pieceAtE2 = result.pieceAt(PGNSquare("e2"))
        val pieceAtE4 = result.pieceAt(PGNSquare("e4"))
        assertNull(pieceAtE2)
        assertEquals(PlayerGamePiece(isBlack = false, PGNGamePiece.Pawn), pieceAtE4)
    }
}