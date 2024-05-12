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

val PGNGamePiece.charValue: Char
    get() = serialValue[0]

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