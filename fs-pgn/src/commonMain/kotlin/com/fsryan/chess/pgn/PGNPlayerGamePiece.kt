@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmInline

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
    return if (isBlack) BlackPlayerGamePieceValue(piece) else WhitePlayerGamePieceValue(piece)
}

@JsExport
@JsName("createPlayerGamePieceFromFENCharCode")
fun FENPlayerGamePiece(fenCharCode: Int): PlayerGamePiece {
    return when (val char = fenCharCode.toChar()) {
        'b', 'k', 'n', 'p', 'q', 'r'-> BlackPlayerGamePieceValue(piece = PGNGamePiece.fromChar(char.uppercaseChar()))
        'B', 'K', 'N', 'P', 'Q', 'R' -> WhitePlayerGamePieceValue(piece = PGNGamePiece.fromChar(char))
        else -> throw IllegalArgumentException("Invalid FEN char code: $fenCharCode")
    }
}

@JvmInline
private value class BlackPlayerGamePieceValue(override val piece: PGNGamePiece): PlayerGamePiece {
    override val isBlack: Boolean
        get() = true
}

@JvmInline
private value class WhitePlayerGamePieceValue(override val piece: PGNGamePiece): PlayerGamePiece {
    override val isBlack: Boolean
        get() = false
}