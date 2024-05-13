@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Wraps a [PGNGamePiece] and a boolean together to represent a player's game
 * piece
 */
@JsExport
interface PlayerGamePiece {
    val isBlack: Boolean
    val piece: PGNGamePiece
}

@JsExport
@JsName("createPlayerGamePiece")
fun PlayerGamePiece(isBlack: Boolean, piece: PGNGamePiece): PlayerGamePiece {
    return PlayerGamePieceData(isBlack = isBlack, piece = piece)
}

@JsExport
@JsName("createPlayerGamePieceFromFENCharCode")
fun FENPlayerGamePiece(fenCharCode: Int): PlayerGamePiece {
    return when (val char = fenCharCode.toChar()) {
        'b', 'k', 'n', 'p', 'q', 'r'-> PlayerGamePieceData(isBlack = true, piece = PGNGamePiece.fromChar(char.uppercaseChar()))
        'B', 'K', 'N', 'P', 'Q', 'R' -> PlayerGamePieceData(isBlack = false, piece = PGNGamePiece.fromChar(char))
        else -> throw IllegalArgumentException("Invalid FEN char code: $fenCharCode")
    }
}

private data class PlayerGamePieceData(override val isBlack: Boolean, override val piece: PGNGamePiece): PlayerGamePiece