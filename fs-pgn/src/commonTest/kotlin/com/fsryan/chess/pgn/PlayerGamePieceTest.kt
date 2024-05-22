package com.fsryan.chess.pgn

import com.fsryan.chess.pgn.PGNGamePiece.Bishop
import com.fsryan.chess.pgn.PGNGamePiece.King
import com.fsryan.chess.pgn.PGNGamePiece.Knight
import com.fsryan.chess.pgn.PGNGamePiece.Pawn
import com.fsryan.chess.pgn.PGNGamePiece.Queen
import com.fsryan.chess.pgn.PGNGamePiece.Rook
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerGamePieceTest {

    @Test
    fun shouldCreateCorrectUnicodeSymbolPawnBlack() {
        assertEquals('♟', Pawn.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolPawnWhite() {
        assertEquals('♙', Pawn.white().unicodeChar)
    }

    @Test
    fun shouldCreatecorrectUnicodeSymbolKnightBlack() {
        assertEquals('♞', Knight.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolKnightWhite() {
        assertEquals('♘', Knight.white().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolBishopBlack() {
        assertEquals('♝', Bishop.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolBishopWhite() {
        assertEquals('♗', Bishop.white().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolRookBlack() {
        assertEquals('♜', Rook.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolRookWhite() {
        assertEquals('♖', Rook.white().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolQueenBlack() {
        assertEquals('♛', Queen.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolQueenWhite() {
        assertEquals('♕', Queen.white().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolKingBlack() {
        assertEquals('♚', King.black().unicodeChar)
    }

    @Test
    fun shouldCreateCorrectUnicodeSymbolKingWhite() {
        assertEquals('♔', King.white().unicodeChar)
    }
}