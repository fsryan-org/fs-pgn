@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.fen

import com.fsryan.chess.pgn.FENPlayerGamePiece
import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.PlayerGamePiece
import com.fsryan.chess.pgn.charValue
import com.fsryan.chess.pgn.file
import com.fsryan.chess.pgn.kingDestinationSquare
import com.fsryan.chess.pgn.ofPlayer
import com.fsryan.chess.pgn.pgnString
import com.fsryan.chess.pgn.previousRank
import com.fsryan.chess.pgn.rookDestinationSquare
import com.fsryan.chess.pgn.sq
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmInline

@JsExport
interface ForsythEdwardsNotation {
    val activePlayerCharacterCode: Int
    val enPassantTargetSquare: PGNSquare?
    val fullMoveNumber: Int
    /**
     * The Halfmove Clock inside an chess position object takes care of
     * enforcing the fifty-move rule. This counter is reset after captures or
     * pawn moves, and incremented otherwise. Also moves which lose the
     * castling rights, that is rook- and king moves from their initial
     * squares, including castling itself, increment the Halfmove Clock.
     * However, those moves are irreversible in the sense to reverse the same
     * rights - since once a castling right is lost, it is lost forever, as
     * considered in detecting repetitions.
     */
    val halfMoveClock: Int
    val serialValue: String

    /**
     * The FEN position field of the Forsyth-Edwards Notation
     */
    val positionsField: String

    fun blackHasCastlingRights(castle: PGNCastle): Boolean
    fun pieceAt(square: PGNSquare): PlayerGamePiece?
    fun whiteHasCastlingRights(castle: PGNCastle): Boolean
}

@JsExport
@JsName("createForsythEdwardsNotation")
fun ForsythEdwardsNotation(serialValue: String): ForsythEdwardsNotation {
    // TODO: ensure string fits format
    return ForsythEdwardsNotationValue(serialValue)
}


@JsExport
@JsName("createForsythEdwardsNotationFrom")
fun ForsythEdwardsNotation(
    positionString: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
    blackIsActive: Boolean = false,
    blackCanCastleKingSide: Boolean = true,
    blackCanCastleQueenSide: Boolean = true,
    enPassantSquare: PGNSquare? = null,
    halfMoveClock: Int = 0,
    fullMoveNumber: Int = 1,
    whiteCanCastleKingSide: Boolean = true,
    whiteCanCastleQueenSide: Boolean = true
): ForsythEdwardsNotation {
    return ForsythEdwardsNotation(serialValue = buildString {
        append(positionString)
        append(' ')

        append(if (blackIsActive) 'b' else 'w')
        append(' ')

        var castlingRightsAdded = false
        if (whiteCanCastleKingSide) {
            append('K')
            castlingRightsAdded = true
        }
        if (whiteCanCastleQueenSide) {
            append('Q')
            castlingRightsAdded = true
        }
        if (blackCanCastleKingSide) {
            append('k')
            castlingRightsAdded = true
        }
        if (blackCanCastleQueenSide) {
            append('q')
            castlingRightsAdded = true
        }
        if (!castlingRightsAdded) {
            append('-')
        }
        append(' ')

        append(enPassantSquare?.pgnString ?: "-")
        append(' ')

        append(halfMoveClock)
        append(' ')

        append(fullMoveNumber)
    })
}

@JsExport
fun ForsythEdwardsNotation.blackKingSquare(): PGNSquare = kingSquare(black = true)
@JsExport
fun ForsythEdwardsNotation.whiteKingSquare(): PGNSquare = kingSquare(black = false)

@JsExport
fun ForsythEdwardsNotation.kingSquare(black: Boolean): PGNSquare {
    return PGNGamePiece.King.ofPlayer(black).let { king ->
        (if (whiteIsActive()) 0..63 else 63 downTo 0)
            .map(::PGNSquare)
            .firstOrNull { square -> pieceAt(square) == king }
            ?: throw IllegalStateException("King not found")
    }
}

@JsExport
const val FEN_STANDARD_STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

@JsExport
fun ForsythEdwardsNotation.blackIsActive(): Boolean = activePlayerCharacterCode == 'b'.code
@JsExport
fun ForsythEdwardsNotation.whiteIsActive(): Boolean = activePlayerCharacterCode == 'w'.code

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
    override val positionsField: String
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

    private fun pieceLocationsOnRank(rank: Int): String {
        return positionsField.split('/')[8 - rank]
    }

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