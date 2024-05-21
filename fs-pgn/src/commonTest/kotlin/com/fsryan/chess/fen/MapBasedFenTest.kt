package com.fsryan.chess.fen

import com.fsryan.chess.pgn.PGNGamePiece.Pawn
import com.fsryan.chess.pgn.PGNGamePiece.Knight
import com.fsryan.chess.pgn.PGNGamePiece.Bishop
import com.fsryan.chess.pgn.PGNGamePiece.Rook
import com.fsryan.chess.pgn.PGNGamePiece.Queen
import com.fsryan.chess.pgn.PGNGamePiece.King
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.black
import com.fsryan.chess.pgn.sq
import com.fsryan.chess.pgn.white
import kotlin.test.Test
import kotlin.test.assertEquals

class MapBasedFenTest {

    @Test
    fun shouldCorrectlyMovePiece() {
        val fen = ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION).ensureMapBased()

        assertEquals(Rook.black(), fen.pieceAt("a8".sq()))
        assertEquals(Knight.black(), fen.pieceAt("b8".sq()))
        assertEquals(Bishop.black(), fen.pieceAt("c8".sq()))
        assertEquals(Queen.black(), fen.pieceAt("d8".sq()))
        assertEquals(King.black(), fen.pieceAt("e8".sq()))
        assertEquals(Bishop.black(), fen.pieceAt("f8".sq()))
        assertEquals(Knight.black(), fen.pieceAt("g8".sq()))
        assertEquals(Rook.black(), fen.pieceAt("h8".sq()))

        assertEquals(Rook.white(), fen.pieceAt("a1".sq()))
        assertEquals(Knight.white(), fen.pieceAt("b1".sq()))
        assertEquals(Bishop.white(), fen.pieceAt("c1".sq()))
        assertEquals(Queen.white(), fen.pieceAt("d1".sq()))
        assertEquals(King.white(), fen.pieceAt("e1".sq()))
        assertEquals(Bishop.white(), fen.pieceAt("f1".sq()))
        assertEquals(Knight.white(), fen.pieceAt("g1".sq()))
        assertEquals(Rook.white(), fen.pieceAt("h1".sq()))

        ('a'..'h').forEach { file ->
            assertEquals(Pawn.black(), fen.pieceAt(PGNSquare(file = file, rank = 7)))
            assertEquals(Pawn.white(), fen.pieceAt(PGNSquare(file = file, rank = 2)))
        }

        assertEquals("e8".sq(), fen.blackKingSquare())
        assertEquals("e1".sq(), fen.whiteKingSquare())
    }
}