@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNCastle(val serialValue: String) {
    KingSide("O-O"),
    QueenSide("O-O-O");

    companion object {
        fun fromSerialValue(serialValue: String): PGNCastle {
            return when (serialValue) {
                KingSide.serialValue -> KingSide
                QueenSide.serialValue -> QueenSide
                else -> throw IllegalArgumentException("No PGNCastleType found for fromSerialValue: $serialValue")
            }
        }
    }
}

val PGNCastle.kingDestinationFile: Char
    get() = if (this == PGNCastle.KingSide) 'g' else 'c'
val PGNCastle.rookDestinationFile: Char
    get() = if (this == PGNCastle.KingSide) 'f' else 'd'

@JsExport
fun PGNCastle.kingDestinationSquare(moveIsBlack: Boolean): PGNSquare {
    return PGNSquare(file = kingDestinationFile, rank = if (moveIsBlack) 8 else 1)
}

@JsExport
fun PGNCastle.rookDestinationSquare(moveIsBlack: Boolean): PGNSquare {
    return PGNSquare(file = rookDestinationFile, rank = if (moveIsBlack) 8 else 1)
}