@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.fen

import com.fsryan.chess.pgn.FENPlayerGamePiece
import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.PlayerGamePiece
import com.fsryan.chess.pgn.charValue
import com.fsryan.chess.pgn.file
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmInline

@JsExport
interface ForsythEdwardsNotation {
    val activePlayerCharacterCode: Int
    val enPassantTargetSquare: PGNSquare?
    val fullMoveNumber: Int
    val halfMoveClock: Int
    val serialValue: String
    fun blackHasCastlingRights(castle: PGNCastle): Boolean
    fun pieceAt(square: PGNSquare): PlayerGamePiece?
    fun whiteHasCastlingRights(castle: PGNCastle): Boolean
}

fun ForsythEdwardsNotation(serialValue: String): ForsythEdwardsNotation {
    // TODO: ensure string fits format
    return ForsythEdwardsNotationValue(serialValue)
}

@JsExport
const val FEN_STANDARD_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

@JsExport
fun ForsythEdwardsNotation.blackIsActive(): Boolean = activePlayerCharacterCode == 'b'.code
@JsExport
fun ForsythEdwardsNotation.whiteIsActive(): Boolean = activePlayerCharacterCode == 'w'.code

@JsExport
fun ForsythEdwardsNotation.replacePieceAt(square: PGNSquare, piece: PlayerGamePiece? = null): ForsythEdwardsNotation {
    pieceAt(square) ?: piece ?: return this

    var emptySpaceCounter = 0
    val buf = StringBuilder()
    sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
        if (file == square.file) {
            piece?.let {
                if (emptySpaceCounter > 0) {
                    buf.append(emptySpaceCounter)
                    emptySpaceCounter = 0
                }
                buf.append(if (it.isBlack) it.piece.charValue.lowercaseChar() else it.piece.charValue)
            } ?: emptySpaceCounter++
        } else {
            pieceAt(PGNSquare(file = file, rank = square.rank))?.let { pgp ->
                if (emptySpaceCounter > 0) {
                    buf.append(emptySpaceCounter)
                    emptySpaceCounter = 0
                }
                buf.append(if (pgp.isBlack) pgp.piece.charValue.lowercaseChar() else pgp.piece.charValue)
            } ?: emptySpaceCounter++
        }
    }
    if (emptySpaceCounter > 0) {
        buf.append(emptySpaceCounter)
    }

    val pieceLocationsList = pieceLocationsField.split('/').toMutableList()
    pieceLocationsList[8 - square.rank] = buf.toString()
    val pieceLocations = pieceLocationsList.joinToString("/")
    val newFENList = serialValue.split(' ').toMutableList()
    newFENList[0] = pieceLocations
    return ForsythEdwardsNotation(newFENList.joinToString(" "))
}

internal val ForsythEdwardsNotation.pieceLocationsField: String
    get() = when(this) {
        is ForsythEdwardsNotationValue -> _pieceLocationsField
        else -> ForsythEdwardsNotationValue(serialValue)._pieceLocationsField
    }

/**
 * Wraps a String that represents a Forsyth-Edwards Notation representation of
 * a chess game state
 */
@JvmInline
private value class ForsythEdwardsNotationValue(override val serialValue: String): ForsythEdwardsNotation {

    private val activePlayerField: String
        get() = fieldAt(1)
    private val castlingRightsField: String
        get() = fieldAt(2)
    private val enPassantTargetSquareField: String
        get() = fieldAt(3)
    private val halfMoveClockField: String
        get() = fieldAt(4)
    private val fullMoveNumberField: String
        get() = fieldAt(5)
    internal val _pieceLocationsField: String
        get() = fieldAt(0)

    override val enPassantTargetSquare: PGNSquare?
        get() = with(enPassantTargetSquareField) {
            if (this == "-") null else PGNSquare(squareString = this)
        }
    override val halfMoveClock: Int
        get() = halfMoveClockField.toInt()
    override val fullMoveNumber: Int
        get() = fullMoveNumberField.toInt()
    override val activePlayerCharacterCode: Int
        get() = activePlayerField[0].code

    override fun blackHasCastlingRights(castle: PGNCastle): Boolean {
        return castlingRightsField.contains(if (castle == PGNCastle.KingSide) 'k' else 'q')
    }

    override fun pieceAt(square: PGNSquare): PlayerGamePiece? {
        return pieceOnFileOfRank(pieceLocationsOnRank(square.rank), square.file)
    }

    override fun whiteHasCastlingRights(castle: PGNCastle): Boolean {
        return castlingRightsField.contains(if (castle == PGNCastle.KingSide) 'K' else 'Q')
    }

    override fun toString(): String = serialValue

    // there are faster ways to implement this than splitting the string
    private fun fieldAt(index: Int): String = serialValue.split(' ')[index]

    private fun pieceOnFileOfRank(piecesOnRank: String, file: Char): PlayerGamePiece? {
        var currentFile = 'a'
        var currentIndex = 0
        while (currentFile <= 'h') {
            val currentChar = piecesOnRank[currentIndex]
            if (currentChar.isDigit()) {
                currentFile += currentChar.digitToInt()
            } else {
                if (currentFile == file) {
                    return FENPlayerGamePiece(currentChar.code)
                }
                currentFile++
            }

            currentIndex++
        }
        return null
    }
}

internal fun ForsythEdwardsNotation.pieceLocationsOnRank(rank: Int): String {
    return pieceLocationsField.split('/')[8 - rank]
}