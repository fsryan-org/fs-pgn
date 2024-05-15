package com.fsryan.chess.pgn.test

import com.fsryan.chess.fen.ForsythEdwardsNotation
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.PlayerGamePiece
import com.fsryan.chess.pgn.isDark
import kotlin.random.Random

fun TestForsythEdwardsNotation(
    blackCanCastleQueenSide: Boolean = Random.nextBoolean(),
    blackCanCastleKingSide: Boolean = Random.nextBoolean(),
    whiteCanCastleQueenSide: Boolean = Random.nextBoolean(),
    whiteCanCastleKingSide: Boolean = Random.nextBoolean(),
    fullMoveNumber: Int = Random.nextInt(until = 512),
    halfMoveClock: Int = Random.nextInt(until = 51),
    activePlayerBlack: Boolean = Random.nextBoolean(),
    producePiece: (
        square: PGNSquare,
        blackKingsPlaced: Int,
        whiteKingsPlaced: Int,
        numBlackPawnsPlaced: Int,
        numWhitePawnsPlaced: Int,
        numBlackRooksPlaced: Int,
        numWhiteRooksPlaced: Int,
        numBlackKnightsPlaced: Int,
        numWhiteKnightsPlaced: Int,
        numBlackDarkSquareBishopsPlaced: Int,
        numWhiteDarkSquareBishopPlaced: Int,
        numBlackLightSquareBishopPlaced: Int,
        numWhiteLightSquareBishopPlaced: Int,
        numBlackQueensPlaced: Int,
        numWhiteQueensPlaced: Int
    ) -> PlayerGamePiece? = {
        square,
        blackKingsPlaced: Int,
        whiteKingsPlaced: Int,
        numBlackPawnsPlaced,
        numWhitePawnsPlaced,
        numBlackRooksPlaced,
        numWhiteRooksPlaced,
        numBlackKnightsPlaced: Int,
        numWhiteKnightsPlaced: Int,
        numBlackDarkSquareBishopsPlaced: Int,
        numWhiteDarkSquareBishopPlaced: Int,
        numBlackLightSquareBishopPlaced: Int,
        numWhiteLightSquareBishopPlaced: Int,
        numBlackQueensPlaced: Int,
        numWhiteQueensPlaced: Int ->
        randomOptional(Random.nextFloat() * 0.4F + 0.5F) { // <-- 10% - 50% chance of placing a piece
            // 25% -50% of the pieces attempted will be pawns
            randomOptional(Random.nextFloat() / 4 + 0.5F) { PGNGamePiece.Pawn }
                ?: PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.random()
        }?.let { piece ->
            when (piece) {
                PGNGamePiece.King -> producePiece(
                    piece = piece,
                    numBlackPlaced = blackKingsPlaced,
                    numWhitePlaced = whiteKingsPlaced,
                    typicalLimit = 1,
                    pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.95F,
                    pctChanceToPlacePieceAfterLimitReached = 0F
                )
                PGNGamePiece.Pawn -> when (square.rank) {
                    1, 8 -> null    // <-- will not place pawns on the first or last rank
                    else -> producePiece(
                        piece = piece,
                        numBlackPlaced = numBlackPawnsPlaced,
                        numWhitePlaced = numWhitePawnsPlaced,
                        typicalLimit = 8,
                        pctChanceBlackWhenNeitherLimitReached = square.rank / 8F,
                        pctChanceToPlacePieceAfterLimitReached = 0F
                    )
                }
                PGNGamePiece.Rook -> producePiece(
                    piece = piece,
                    numBlackPlaced = numBlackRooksPlaced,
                    numWhitePlaced = numWhiteRooksPlaced,
                    typicalLimit = 2,
                    pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.9F,
                    pctChanceToPlacePieceAfterLimitReached = 0.01F
                )
                PGNGamePiece.Knight -> producePiece(
                    piece = piece,
                    numBlackPlaced = numBlackKnightsPlaced,
                    numWhitePlaced = numWhiteKnightsPlaced,
                    typicalLimit = 2,
                    pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.9F,
                    pctChanceToPlacePieceAfterLimitReached = 0.01F
                )
                PGNGamePiece.Bishop -> when (square.isDark()) {
                    true -> producePiece(
                        piece = piece,
                        numBlackPlaced = numBlackDarkSquareBishopsPlaced,
                        numWhitePlaced = numWhiteDarkSquareBishopPlaced,
                        typicalLimit = 1,
                        pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.9F,
                        pctChanceToPlacePieceAfterLimitReached = 0.01F
                    )
                    false -> producePiece(
                        piece = piece,
                        numBlackPlaced = numBlackLightSquareBishopPlaced,
                        numWhitePlaced = numWhiteLightSquareBishopPlaced,
                        typicalLimit = 1,
                        pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.9F,
                        pctChanceToPlacePieceAfterLimitReached = 0.01F
                    )
                }
                PGNGamePiece.Queen -> producePiece(
                    piece = piece,
                    numBlackPlaced = numBlackQueensPlaced,
                    numWhitePlaced = numWhiteQueensPlaced,
                    typicalLimit = 1,
                    pctChanceBlackWhenNeitherLimitReached = square.rank / 8F * 0.9F,
                    pctChanceToPlacePieceAfterLimitReached = 0.1F
                )
            }
        }
    },
    forcedEnPassantTarget: PGNSquare? = null,
): ForsythEdwardsNotation {
    var blackKingsPlaced = 0
    var whiteKingsPlaced = 0
    var numBlackPawnsPlaced = 0
    var numWhitePawnsPlaced = 0
    var numBlackRooksPlaced = 0
    var numWhiteRooksPlaced = 0
    var numBlackKnightsPlaced = 0
    var numWhiteKnightsPlaced = 0
    var numBlackDarkSquareBishopsPlaced = 0
    var numWhiteDarkSquareBishopPlaced = 0
    var numBlackLightSquareBishopPlaced = 0
    var numWhiteLightSquareBishopPlaced = 0
    var numBlackQueensPlaced = 0
    var numWhiteQueensPlaced = 0
    var consecutiveEmptySquares = 0
    val potentialEnPassantSquares = mutableSetOf<PGNSquare>()
    val fenBuf = StringBuilder()
    sequenceOf(8, 7, 6, 7, 4, 3, 2, 1).forEach { rank ->
        if (rank != 8) {
            if (consecutiveEmptySquares > 0) {
                fenBuf.append(consecutiveEmptySquares)
                consecutiveEmptySquares = 0
            }
            fenBuf.append('/')
        }
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            val square = PGNSquare(file = file, rank = rank)
            val piece = producePiece(
                square,
                blackKingsPlaced,
                whiteKingsPlaced,
                numBlackPawnsPlaced,
                numWhitePawnsPlaced,
                numBlackRooksPlaced,
                numWhiteRooksPlaced,
                numBlackKnightsPlaced,
                numWhiteKnightsPlaced,
                numBlackDarkSquareBishopsPlaced,
                numWhiteDarkSquareBishopPlaced,
                numBlackLightSquareBishopPlaced,
                numWhiteLightSquareBishopPlaced,
                numBlackQueensPlaced,
                numWhiteQueensPlaced
            )
            piece?.let {
                if (consecutiveEmptySquares > 0) {
                    fenBuf.append(consecutiveEmptySquares)
                    consecutiveEmptySquares = 0
                }
                when (it.piece) {
                    PGNGamePiece.King -> when (it.isBlack) {
                        true -> fenBuf.append('k').also { blackKingsPlaced++ }
                        false -> fenBuf.append('K').also { whiteKingsPlaced++ }
                    }
                    PGNGamePiece.Pawn -> when (it.isBlack) {
                        true -> fenBuf.append('p').also {
                            numBlackPawnsPlaced++
                            if (square.rank == 5 && Random.nextBoolean()) {
                                potentialEnPassantSquares.add(square)
                            }
                        }
                        false -> fenBuf.append('P').also {
                            numWhitePawnsPlaced++
                            if (square.rank == 4 && Random.nextBoolean()) {
                                potentialEnPassantSquares.add(square)
                            }
                        }
                    }
                    PGNGamePiece.Rook -> when (it.isBlack) {
                        true -> fenBuf.append('r').also { numBlackRooksPlaced++ }
                        false -> fenBuf.append('R').also { numWhiteRooksPlaced++ }
                    }
                    PGNGamePiece.Knight -> when (it.isBlack) {
                        true -> fenBuf.append('n').also { numBlackKnightsPlaced++ }
                        false -> fenBuf.append('N').also { numWhiteKnightsPlaced++ }
                    }
                    PGNGamePiece.Bishop -> when (it.isBlack) {
                        true -> fenBuf.append('b').also {
                            when (square.isDark()) {
                                true -> numBlackDarkSquareBishopsPlaced++
                                false -> numBlackLightSquareBishopPlaced++
                            }
                        }
                        false -> fenBuf.append('B').also {
                            when (square.isDark()) {
                                true -> numWhiteDarkSquareBishopPlaced++
                                false -> numWhiteLightSquareBishopPlaced++
                            }
                        }
                    }
                    PGNGamePiece.Queen -> when (it.isBlack) {
                        true -> fenBuf.append('q').also { numBlackQueensPlaced++ }
                        false -> fenBuf.append('Q').also { numWhiteQueensPlaced++ }
                    }
                }
            } ?: consecutiveEmptySquares++
        }
    }
    if (consecutiveEmptySquares > 0) {
        fenBuf.append(consecutiveEmptySquares)
    }
    fenBuf.append(' ')

    fenBuf.append(if (activePlayerBlack) 'b' else 'w')
    fenBuf.append(' ')

    var requiresDashForCastling = true
    if (whiteCanCastleKingSide) {
        fenBuf.append('K')
        requiresDashForCastling = false
    }
    if (whiteCanCastleQueenSide) {
        fenBuf.append('Q')
        requiresDashForCastling = false
    }
    if (blackCanCastleKingSide) {
        fenBuf.append('k')
        requiresDashForCastling = false
    }
    if (blackCanCastleQueenSide) {
        fenBuf.append('q')
        requiresDashForCastling = false
    }
    if (requiresDashForCastling) {
        fenBuf.append('-')
    }
    fenBuf.append(' ')

    forcedEnPassantTarget?.let {
        fenBuf.append(it.toString())
    } ?: when (potentialEnPassantSquares.isNotEmpty()) {
        true -> fenBuf.append(potentialEnPassantSquares.random().toString())
        false -> fenBuf.append('-')
    }
    fenBuf.append(' ')

    fenBuf.append(halfMoveClock)
    fenBuf.append(' ')

    fenBuf.append(fullMoveNumber)

    val fenString = fenBuf.toString()
    return ForsythEdwardsNotation(fenString)
}

private fun producePiece(
    piece: PGNGamePiece,
    numBlackPlaced: Int,
    numWhitePlaced: Int,
    typicalLimit: Int,
    pctChanceBlackWhenNeitherLimitReached: Float,
    pctChanceToPlacePieceAfterLimitReached: Float = 0.05F
): PlayerGamePiece? {
    return when (numBlackPlaced >= typicalLimit) {
        true -> when (numWhitePlaced >= typicalLimit) {
            true -> randomOptional(1F - pctChanceToPlacePieceAfterLimitReached) {
                PlayerGamePiece(
                    isBlack = Random.nextFloat() < 0.5 * numBlackPlaced / numWhitePlaced.toFloat(),
                    piece = piece
                )
            }
            false -> PlayerGamePiece(isBlack = false, piece = piece)
        }
        false -> when (numWhitePlaced >= typicalLimit) {
            true -> PlayerGamePiece(isBlack = true, piece = piece)
            false -> PlayerGamePiece(isBlack = Random.nextFloat() < pctChanceBlackWhenNeitherLimitReached, piece = piece)
        }
    }
}