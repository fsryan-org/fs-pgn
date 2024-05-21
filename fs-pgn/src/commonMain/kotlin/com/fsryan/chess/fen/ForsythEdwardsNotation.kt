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
    fun blackHasCastlingRights(castle: PGNCastle): Boolean
    fun pieceAt(square: PGNSquare): PlayerGamePiece?
    fun whiteHasCastlingRights(castle: PGNCastle): Boolean

    fun movePiece(from: PGNSquare, to: PGNSquare, promotionPiece: PGNGamePiece? = null): ForsythEdwardsNotation

    fun performCastle(castle: PGNCastle): ForsythEdwardsNotation
}

@JsExport
@JsName("createForsythEdwardsNotation")
fun ForsythEdwardsNotation(serialValue: String): ForsythEdwardsNotation {
    // TODO: ensure string fits format
    return ForsythEdwardsNotationValue(serialValue)
}

@JsExport
fun ForsythEdwardsNotation.blackKingSquare(): PGNSquare = kingSquare(black = true)
@JsExport
fun ForsythEdwardsNotation.whiteKingSquare(): PGNSquare = kingSquare(black = false)

@JsExport
fun ForsythEdwardsNotation.kingSquare(black: Boolean): PGNSquare = when (this) {
    is MapBasedFEN -> if (black) blackKingSquare else whiteKingSquare
    else -> PGNGamePiece.King.ofPlayer(black).let { king ->
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

internal fun ForsythEdwardsNotation.ensureMapBased(): ForsythEdwardsNotation {
    if (this is MapBasedFEN) {
        return this
    }

    var blackKingSquare: PGNSquare? = null
    var whiteKingSquare: PGNSquare? = null
    val pieces = mutableMapOf<Int, PlayerGamePiece>().apply {
        (0..63).map(::PGNSquare).forEach { square ->
            pieceAt(square)?.let {
                if (it.piece == PGNGamePiece.King) {
                    if (it.isBlack) {
                        if (blackKingSquare != null) {
                            throw IllegalStateException("Multiple black kings found")
                        }
                        blackKingSquare = square
                    } else {
                        if (whiteKingSquare != null) {
                            throw IllegalStateException("Multiple white kings found")
                        }
                        whiteKingSquare = square
                    }
                }
                put(square.numericValue, it)
            }
        }
    }
    return MapBasedFEN(
        blackIsActive = blackIsActive(),
        blackCastlingRights = PGNCastle.entries.filter(::blackHasCastlingRights).toSet(),
        blackKingSquare = checkNotNull(blackKingSquare) { "No black king found" },
        enPassantTargetSquare = enPassantTargetSquare,
        fullMoveNumber = fullMoveNumber,
        halfMoveClock = halfMoveClock,
        pieces = pieces.toMap(),
        whiteCastlingRights = PGNCastle.entries.filter(::whiteHasCastlingRights).toSet(),
        whiteKingSquare = checkNotNull(whiteKingSquare) { "No white king found" }
    )
}


// TODO: this prevents variants like Chess960. You'll need to not make this
//  class understand the starting squares of the rooks and king in order to
//  support Chess960
private class MapBasedFEN(
    private val blackIsActive: Boolean,
    private val blackCastlingRights: Set<PGNCastle>,
    internal val blackKingSquare: PGNSquare,
    override val enPassantTargetSquare: PGNSquare?,
    override val fullMoveNumber: Int,
    override val halfMoveClock: Int,
    private val pieces: Map<Int, PlayerGamePiece>,
    private val whiteCastlingRights: Set<PGNCastle>,
    internal val whiteKingSquare: PGNSquare
): ForsythEdwardsNotation {
    override val activePlayerCharacterCode: Int
        get() = if (blackIsActive) 'b'.code else 'w'.code
    override val serialValue: String
        get() = buildString {
            var emptySpaceCounter = 0
            var currentRank = 8
            (8 downTo 1).flatMap { rank ->
                ('a' .. 'h').map { file -> PGNSquare(file = file, rank = rank) }
            }.forEach { square ->
                if (currentRank != square.rank) {
                    if (emptySpaceCounter > 0) {
                        append(emptySpaceCounter)
                        emptySpaceCounter = 0
                    }
                    append('/')
                    currentRank--
                }

                pieceAt(square)?.let {
                    if (emptySpaceCounter > 0) {
                        append(emptySpaceCounter)
                        emptySpaceCounter = 0
                    }
                    append(if (it.isBlack) it.piece.charValue.lowercaseChar() else it.piece.charValue)
                } ?: emptySpaceCounter++
            }
            if (emptySpaceCounter > 0) {
                append(emptySpaceCounter)
            }

            append(' ')
            append(if (whiteIsActive()) 'w' else 'b')

            append(' ')
            var appendedCastlingRights = false
            if (whiteHasCastlingRights(PGNCastle.KingSide)) {
                append('K')
                appendedCastlingRights = true
            }
            if (whiteHasCastlingRights(PGNCastle.QueenSide)) {
                append('Q')
                appendedCastlingRights = true
            }
            if (blackHasCastlingRights(PGNCastle.KingSide)) {
                append('k')
                appendedCastlingRights = true
            }
            if (blackHasCastlingRights(PGNCastle.QueenSide)) {
                append('q')
                appendedCastlingRights = true
            }
            if (!appendedCastlingRights) {
                append('-')
            }

            append(' ')
            enPassantTargetSquare?.let { append(it.pgnString) } ?: append('-')

            append(' ')
            append(halfMoveClock)

            append(' ')
            append(fullMoveNumber)
        }

    override fun blackHasCastlingRights(castle: PGNCastle): Boolean = blackCastlingRights.contains(castle)

    override fun pieceAt(square: PGNSquare): PlayerGamePiece? = pieces[square.numericValue]

    override fun whiteHasCastlingRights(castle: PGNCastle): Boolean = whiteCastlingRights.contains(castle)
    override fun movePiece(from: PGNSquare, to: PGNSquare, promotionPiece: PGNGamePiece?): ForsythEdwardsNotation {
        val newMap = pieces.toMutableMap()
        val moved = newMap.remove(from.numericValue) ?: throw IllegalArgumentException("No piece at $from")
        newMap[to.numericValue] = promotionPiece?.ofPlayer(isBlack = blackIsActive) ?: moved

        val newBlackCastlingRights = when (blackIsActive) {
            true -> when (from) {
                "e8".sq() -> emptySet()
                "a8".sq() -> if (blackHasCastlingRights(PGNCastle.QueenSide)) blackCastlingRights - PGNCastle.QueenSide else blackCastlingRights
                "h8".sq() -> if (blackHasCastlingRights(PGNCastle.KingSide)) blackCastlingRights - PGNCastle.KingSide else blackCastlingRights
                else -> blackCastlingRights
            }
            false -> blackCastlingRights
        }
        val newEnPassantTargetSquare = when (enPassantTargetSquare) {
            null -> when (moved.piece) {
                PGNGamePiece.Pawn -> when (blackIsActive) {
                    true -> if (from.rank == 7 && to.rank == 5) checkNotNull(from.previousRank()) else null
                    false -> if (from.rank == 2 && to.rank == 4) checkNotNull(from.previousRank()) else null
                }
                else -> null
            }
            else -> null
        }
        val newWhiteCastlingRights = when (blackIsActive) {
            true -> whiteCastlingRights
            false -> when (from) {
                "e1".sq() -> emptySet()
                "a1".sq() -> if (whiteHasCastlingRights(PGNCastle.QueenSide)) whiteCastlingRights - PGNCastle.QueenSide else whiteCastlingRights
                "h1".sq() -> if (whiteHasCastlingRights(PGNCastle.KingSide)) whiteCastlingRights - PGNCastle.KingSide else whiteCastlingRights
                else -> whiteCastlingRights
            }
        }
        return MapBasedFEN(
            blackIsActive = !blackIsActive,
            blackCastlingRights = newBlackCastlingRights,
            blackKingSquare = when (blackIsActive) {
                true -> if (moved.piece == PGNGamePiece.King) to else blackKingSquare
                false -> blackKingSquare
            },
            enPassantTargetSquare = newEnPassantTargetSquare,
            fullMoveNumber = fullMoveNumber + if (blackIsActive) 1 else 0,
            halfMoveClock = when {
                moved.piece == PGNGamePiece.Pawn || pieceAt(to) != null -> 0    // <-- pawn move or capture
                else -> halfMoveClock + 1
            },
            pieces = newMap.toMap(),
            whiteCastlingRights = newWhiteCastlingRights,
            whiteKingSquare = when (blackIsActive) {
                true -> whiteKingSquare
                false -> if (moved.piece == PGNGamePiece.King) to else whiteKingSquare
            }
        )
    }

    override fun performCastle(castle: PGNCastle): ForsythEdwardsNotation {
        val rookStartSquare = when (blackIsActive) {
            true -> when (castle) {
                PGNCastle.KingSide -> "h8"
                PGNCastle.QueenSide -> "a8"
            }
            false -> when (castle) {
                PGNCastle.KingSide -> "h1"
                PGNCastle.QueenSide -> "a1"
            }
        }.sq()
        val kingStartSquare = PGNSquare(file = 'e', rank = if (blackIsActive) 8 else 1)

        val newMap = pieces.toMutableMap()
        val rook = newMap.remove(rookStartSquare.numericValue) ?: throw IllegalArgumentException("No piece at $rookStartSquare")
        val king = newMap.remove(kingStartSquare.numericValue) ?: throw IllegalArgumentException("No piece at $kingStartSquare")
        newMap[castle.kingDestinationSquare(blackIsActive).numericValue] = king
        newMap[castle.rookDestinationSquare(blackIsActive).numericValue] = rook

        return MapBasedFEN(
            blackIsActive = !blackIsActive,
            blackCastlingRights = if (blackIsActive) emptySet() else blackCastlingRights,
            blackKingSquare = if (blackIsActive) castle.kingDestinationSquare(true) else blackKingSquare,
            enPassantTargetSquare = null,
            fullMoveNumber = fullMoveNumber + if (blackIsActive) 1 else 0,
            halfMoveClock = halfMoveClock + 1,
            pieces = newMap.toMap(),
            whiteCastlingRights = if (blackIsActive) whiteCastlingRights else emptySet(),
            whiteKingSquare = if (blackIsActive) whiteKingSquare else castle.kingDestinationSquare(false)
        )
    }

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
    private val pieceLocationsField: String
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

    override fun movePiece(from: PGNSquare, to: PGNSquare, promotionPiece: PGNGamePiece?): ForsythEdwardsNotation {
        return ensureMapBased().movePiece(from, to, promotionPiece)
    }

    override fun performCastle(castle: PGNCastle): ForsythEdwardsNotation {
        return ensureMapBased().performCastle(castle)
    }

    override fun toString(): String = serialValue

    // there are faster ways to implement this than splitting the string
    private fun fieldAt(index: Int): String = serialValue.split(' ')[index]

    private fun pieceLocationsOnRank(rank: Int): String {
        return pieceLocationsField.split('/')[8 - rank]
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