@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmInline

@JsExport
interface PGNSquare {
    val fileASCII: Int
    val numericValue: Int
    val rank: Int
}

/**
 * @return true if the square is light, false otherwise
 */
@JsExport
fun PGNSquare.isLight(): Boolean = numericValue % 2 == 0

/**
 * @return true if the square is dark, false otherwise
 */
@JsExport
fun PGNSquare.isDark(): Boolean = !isLight()

val PGNSquare.pgnString: String
    get() = "${file}${rank}"

/**
 * @return the square that is represented by the given two-character
 */
@JsExport
@JsName("createPGNSquareFromString")
fun PGNSquare(squareString: String): PGNSquare {
    if (squareString.length != 2) {
        throw IllegalArgumentException("squareString must be exactly 2 characters long")
    }
    return PGNSquare(squareString[0], squareString[1].toString().toInt())
}

val PGNSquare.file: Char
    get() = fileASCII.toChar()

/**
 * @return the square that is represented by the given file and rank
 */
fun PGNSquare(file: Char, rank: Int): PGNSquare {
    val fileVal = when (file) {
        'a', 'A' -> 0
        'b', 'B' -> 1
        'c', 'C' -> 2
        'd', 'D' -> 3
        'e', 'E' -> 4
        'f', 'F' -> 5
        'g', 'G' -> 6
        'h', 'H' -> 7
        else -> throw IllegalArgumentException("file must be between 'a' and 'h' (case insensitive)")
    }
    val rankVal = when (rank) {
        in 1..8 -> rank - 1
        else -> throw IllegalArgumentException("rank must be between 1 and 8")
    }
    return PGNSquare(rankVal * 8 + fileVal)
}

@JsExport
@JsName("createPGNSquareFromCharASCIIAndInt")
fun PGNSquare(fileASCII: Int, rank: Int): PGNSquare {
    if (fileASCII < 'a'.code || fileASCII > 'h'.code) {
        throw IllegalArgumentException("fileASCII must be between 'a' and 'h' (case sensitive)")
    }
    return PGNSquare(fileASCII.toChar(), rank)
}

/**
 * @return the square that is represented by the given numeric value
 */
@JsExport
@JsName("createPGNSquareFromInt")
fun PGNSquare(value: Int): PGNSquare {
    if (value < 0 || value > 63) {
        throw IllegalArgumentException("value must be between 0 and 63")
    }
    return PGNSquareValue(value)
}

@JvmInline
private value class PGNSquareValue(override val numericValue: Int): PGNSquare {
    override val fileASCII: Int
        get() = when (numericValue % 8) {
            0 -> 'a'.code
            1 -> 'b'.code
            2 -> 'c'.code
            3 -> 'd'.code
            4 -> 'e'.code
            5 -> 'f'.code
            6 -> 'g'.code
            7 -> 'h'.code
            else -> throw IllegalStateException("numericValue must be positive or 0")
        }
    override val rank: Int
        get() = numericValue / 8 + 1

    override fun toString(): String {
        return "${file}${rank}"
    }
}
