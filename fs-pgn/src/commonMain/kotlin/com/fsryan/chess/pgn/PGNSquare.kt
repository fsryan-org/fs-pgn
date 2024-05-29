@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmInline
import kotlin.math.abs

@JsExport
interface PGNSquare {
    val fileASCII: Int
    val numericValue: Int
    val rank: Int
}

/**
 * This is a shortcut that lessens the verbosity of creating a [PGNSquare]
 * @return the [PGNSquare] described by the string
 * @throws IllegalArgumentException if the string is not exactly 2 characters long
 * or if the file is not between 'a' and 'h' (case insensitive) or if the rank
 * is not between 1 and 8
 * @see PGNSquare
 */
@JsExport
fun String.sq(): PGNSquare = PGNSquare(this)

/**
 * @return true if the square is light, false otherwise
 */
@JsExport
fun PGNSquare.isLight(): Boolean = when ((numericValue / 8) % 2 == 0) {
    true -> numericValue % 2 == 0   // <-- ranks 1, 3, 5, 7 start on dark squares
    false -> numericValue % 2 == 1  // <-- ranks 2, 4, 6, 8 start on light squares
}

/**
 * @return true if the square is dark, false otherwise
 */
@JsExport
fun PGNSquare.isDark(): Boolean = !isLight()

@JsExport
fun PGNSquare.isOnSameDiagonal(other: PGNSquare): Boolean = abs(other.file - file) == abs(other.rank - rank)

/**
 * @return the square at the next diagonal in the direction specified by the
 * parameters or null if it is off the board
 */
@JsExport
fun PGNSquare.nextDiagonal(left: Boolean, up: Boolean): PGNSquare? {
    val file = if (left) previousFile() else nextFile()
    return file?.let { if (up) it.nextRank() else it.previousRank() }
}

/**
 * @return the square at the next rank or null if it is off the board
 */
@JsExport
fun PGNSquare.nextRank(): PGNSquare? = if (rank == 8) null else PGNSquare(numericValue + 8)

/**
 * @return the square at the previous rank or null if it is off the board
 */
@JsExport
fun PGNSquare.previousRank(): PGNSquare? = if (rank == 1) null else PGNSquare(numericValue - 8)

/**
 * @return the square at the next file or null if it is off the board
 */
@JsExport
fun PGNSquare.nextFile(): PGNSquare? = if (file == 'h') null else PGNSquare(numericValue + 1)

/**
 * @return the square at the previous file or null if it is off the board
 */
@JsExport
fun PGNSquare.previousFile(): PGNSquare? = if (file == 'a') null else PGNSquare(numericValue - 1)

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
    return PGNSquare(file = squareString[0], rank = squareString[1].digitToInt())
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
    return PGNSquare(file = fileASCII.toChar(), rank = rank)
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
    return PGNSquareUnsafe(value)
}

/**
 * This is unsafe because it assumes the caller has already checked for a valid
 * range on the integer [0, 64)
 *
 * @return the square that is represented by the given numeric value
 */
@JsExport
@JsName("createPGNSquareUnsafe")
fun PGNSquareUnsafe(value: Int): PGNSquare = PGNSquareValue(value)

@JvmInline
private value class PGNSquareValue(override val numericValue: Int): PGNSquare {
    override val fileASCII: Int
        get() = 'a'.code + (numericValue % 8)
    override val rank: Int
        get() = numericValue / 8 + 1
    override fun toString(): String {
        return "${file}${rank}"
    }
}
