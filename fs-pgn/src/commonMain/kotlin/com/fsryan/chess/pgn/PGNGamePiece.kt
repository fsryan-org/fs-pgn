@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNGamePiece(val serialValue: String, val isSerialValueOptional: Boolean) {
    Pawn("P", isSerialValueOptional = true),
    Knight("N", isSerialValueOptional = false),
    Bishop("B", isSerialValueOptional = false),
    Rook("R", isSerialValueOptional = false),
    Queen("Q", isSerialValueOptional = false),
    King("K", isSerialValueOptional = false);

    companion object {
        fun fromSerialValue(serialValue: String): PGNGamePiece {
            return when (serialValue) {
                Pawn.serialValue, "" -> Pawn
                Knight.serialValue -> Knight
                Bishop.serialValue -> Bishop
                Rook.serialValue -> Rook
                Queen.serialValue -> Queen
                King.serialValue -> King
                else -> throw IllegalArgumentException("No PGNGamePiece found for serialValue: $serialValue")
            }
        }
    }
}

/**
 * A shortcut for creating a [PlayerGamePiece] with the [PGNGamePiece] as the
 * piece and the player as black
 * @return a [PlayerGamePiece] with the [PGNGamePiece] as the piece and the
 * player as black
 */
@JsExport
fun PGNGamePiece.black(): PlayerGamePiece = PlayerGamePiece(isBlack = true, piece = this)

/**
 * A shortcut for creating a [PlayerGamePiece] with the [PGNGamePiece] as the
 * piece and the player as white
 * @return a [PlayerGamePiece] with the [PGNGamePiece] as the piece and the
 * player as white
 */
@JsExport
fun PGNGamePiece.white(): PlayerGamePiece = PlayerGamePiece(isBlack = false, piece = this)

/**
 * A shortcut for creating a [PlayerGamePiece] with the [PGNGamePiece] as the piece
 * and the player as the specified value
 *
 * @param isBlack true if the player is black, false if the player is white
 * @return a [PlayerGamePiece] with the [PGNGamePiece] as the piece and the
 * player as the specified value
 */
@JsExport
fun PGNGamePiece.ofPlayer(isBlack: Boolean): PlayerGamePiece = PlayerGamePiece(isBlack = isBlack, piece = this)

@JsExport
fun PGNGamePiece.blackUnicodeSymbolCode(): Int = unicodeSymbolCode(isBlack = true)
@JsExport
fun PGNGamePiece.whiteUnicodeSymbolCode(): Int = unicodeSymbolCode(isBlack = false)
@JsExport
fun PGNGamePiece.unicodeSymbolCode(isBlack: Boolean): Int = unicodeSymbolCodeOffset(if (isBlack) 6 else 0)

val PGNGamePiece.blackUnicodeSymbolChar: Char
    get() = blackUnicodeSymbolCode().toChar()
val PGNGamePiece.whiteUnicodeSymbolChar: Char
    get() = whiteUnicodeSymbolCode().toChar()
fun PGNGamePiece.unicodeSymbolChar(isBlack: Boolean): Char = unicodeSymbolCode(isBlack).toChar()

/**
 * the character value of the [PGNGamePiece]
 */
val PGNGamePiece.charValue: Char
    get() = serialValue[0]

/**
 * @return the [PGNGamePiece] that corresponds to the given character
 */
fun PGNGamePiece.Companion.fromChar(char: Char): PGNGamePiece {
    return when (char) {
        PGNGamePiece.Pawn.charValue -> PGNGamePiece.Pawn
        PGNGamePiece.Knight.charValue -> PGNGamePiece.Knight
        PGNGamePiece.Bishop.charValue -> PGNGamePiece.Bishop
        PGNGamePiece.Rook.charValue -> PGNGamePiece.Rook
        PGNGamePiece.Queen.charValue -> PGNGamePiece.Queen
        PGNGamePiece.King.charValue -> PGNGamePiece.King
        else -> throw IllegalArgumentException("No PGNGamePiece found for char: $char")
    }
}

private fun PGNGamePiece.unicodeSymbolCodeOffset(offset: Int): Int {
    val whiteKing = 0x2654
    val whiteOffset = when (this) {
        PGNGamePiece.King -> 0
        PGNGamePiece.Queen -> 1
        PGNGamePiece.Rook -> 2
        PGNGamePiece.Bishop -> 3
        PGNGamePiece.Knight -> 4
        PGNGamePiece.Pawn -> 5
    }
    return whiteKing + whiteOffset + offset
}