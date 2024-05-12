package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNSANMoveSuffixAnnotation
import com.fsryan.chess.pgn.PGNSANIllegalCharacterException
import com.fsryan.chess.pgn.PGNSANMove
import com.fsryan.chess.pgn.PGNSANPieceDoesNotPromoteException
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.fromAnnotationText
import com.fsryan.chess.pgn.fromChar
import com.fsryan.chess.pgn.kingDestinationSquare
import okio.BufferedSource
import okio.IOException
import okio.use
import kotlin.js.JsName
import kotlin.jvm.JvmInline

internal interface PGNSANMoveParser: PGNParser<PGNSANMove> {
    val moveIsBlack: Boolean
}

@JsName("createPGNSANMoveParser")
internal fun PGNSANMoveParser(moveIsBlack: Boolean): PGNSANMoveParser {
    return PGNSANMoveParserValue(moveIsBlack)
}

@JvmInline
private value class PGNSANMoveParserValue(override val moveIsBlack: Boolean): PGNSANMoveParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNSANMove> {
        if (bufferedSource.exhausted()) {
            throw PGNParseException(position, "Unexpected end of file while reading PGN SAN move")
        }

        try {
            bufferedSource.peek().use { peekable ->
                val firstChar = peekable.readUTF8Char()
                val result = when {
                    firstChar.isFile -> peekable.continuePreDisambiguation(
                        startPosition = position,
                        charsRead = 1,
                        piece = PGNGamePiece.Pawn,
                        file = firstChar,
                        rank = null
                    )
                    firstChar.isPiece -> peekable.continuePreFile(
                        startPosition = position,
                        charsRead = 1,
                        piece = PGNGamePiece.fromChar(firstChar)
                    )
                    firstChar.isCastleMarker -> peekable.castle(position, charsRead = 1)
                    else -> throw PGNSANIllegalCharacterException(position, firstChar)
                }
                bufferedSource.incrementByUTF8CharacterCount(result.charactersRead)
                return result
            }
        } catch (ioe: IOException) {
            throw PGNParseException(position, "Failed to read PGN SAN move", ioe)
        }
    }

    // This cannot result in a full move
    private fun BufferedSource.continuePreFile(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            throw PGNParseException(startPosition + charsRead, "Unexpected end of file while reading PGN SAN move")
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isCapture -> continuePostDisambiguation(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                isCapture = true,
                sourceFile = null,
                sourceRank = null,
                destFile = null,
                destRank = null
            )
            nextChar.isFile -> continuePreDisambiguation(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                file = nextChar,
                rank = null
            )
            nextChar.isRank -> continuePreDisambiguation(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                file = null,
                rank = nextChar.digitToInt()
            )
            else -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
        }
    }

    // This can result in a full move
    // Here, we don't know whether we're reading the destination file and rank or the source file and rank
    private fun BufferedSource.continuePreDisambiguation(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        file: Char?,
        rank: Int?
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            if (file == null || rank == null) {
                throw PGNParseException(startPosition + charsRead, "Unexpected end of file while reading PGN SAN move")
            }
            return PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = file, rank = rank),
                    piece = piece,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isCapture -> continuePostDisambiguation(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                isCapture = true,
                sourceFile = file,
                sourceRank = rank,
                destFile = null,
                destRank = null
            )
            nextChar.isFile -> when (file) {
                null -> when (rank) {
                    null -> continuePreDisambiguation(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        file = nextChar,
                        rank = null
                    )
                    else -> continuePostDisambiguation(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        isCapture = false,
                        sourceFile = null,
                        sourceRank = rank,
                        destFile = nextChar,
                        destRank = null
                    )
                }
                else -> continuePostDisambiguation(
                    startPosition = startPosition,
                    charsRead = charsRead + 1,
                    piece = piece,
                    isCapture = false,
                    sourceFile = file,
                    sourceRank = rank,
                    destFile = nextChar,
                    destRank = null
                )
            }
            nextChar.isRank -> when (file) {
                null -> when (rank) {
                    // It may not be possible to get here.
                    null -> continuePostDisambiguation(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        isCapture = false,
                        sourceFile = null,
                        sourceRank = nextChar.digitToInt(),
                        destFile = null,
                        destRank = null
                    )
                    else -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)  // <-- two ranks with no files
                }
                else -> when (rank) {
                    null -> continuePreDisambiguation(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        file = file,
                        rank = nextChar.digitToInt()
                    )
                    else -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)  // <-- two ranks
                }
            }
            nextChar.isPromotion -> when (file) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (rank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> continueWithPromotion(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        isCapture = false,
                        sourceFile = null,
                        sourceRank = null,
                        dest = PGNSquare(file = file, rank = rank)
                    )
                }
            }
            nextChar.isCheckStatus -> when (file) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (rank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> handleCheckStatus(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        promotionPiece = null,
                        castleType = null,
                        isCapture = false,
                        sourceFile = null,
                        sourceRank = null,
                        dest = PGNSquare(file = file, rank = rank),
                        checkStatus = PGNCheckStatus.fromChar(nextChar)
                    )
                }
            }
            nextChar.isSuffixAnnotationSymbol -> when (file) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (rank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> continueWithSuffixAnnotationSymbol(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        castleType = null,
                        checkStatus = PGNCheckStatus.None,
                        piece = piece,
                        promotionPiece = null,
                        isCapture = false,
                        sourceFile = null,
                        sourceRank = null,
                        dest = PGNSquare(file = file, rank = rank),
                        suffixAnnotationSymbol = nextChar
                    )
                }
            }
            else -> when (file) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (rank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> PGNFSMResult(
                        charactersRead = charsRead,
                        value = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = file, rank = rank),
                            piece = piece,
                            promotionPiece = null,
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                    )
                }
            }
        }
    }

    // This can result in a full move
    // here, we know whether we're capturing or not, so the file and rank read here will definitely be the destination
    private fun BufferedSource.continuePostDisambiguation(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        isCapture: Boolean,
        sourceFile: Char?,
        sourceRank: Int?,
        destFile: Char?,
        destRank: Int?
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            if (destFile == null || destRank == null) {
                throw PGNParseException(startPosition + charsRead, "Unexpected end of file while reading PGN SAN move")
            }
            return PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = destFile, rank = destRank),
                    piece = piece,
                    promotionPiece = null,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isFile -> when (destFile) {
                null -> continuePostDisambiguation(
                    startPosition = startPosition,
                    charsRead = charsRead + 1,
                    piece = piece,
                    isCapture = isCapture,
                    sourceFile = sourceFile,
                    sourceRank = sourceRank,
                    destFile = nextChar,
                    destRank = destRank
                )
                else -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
            }
            nextChar.isRank -> when (destFile) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)  // destination Rank before file
                else -> when (destRank) {
                    null -> continuePostDisambiguation(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        isCapture = isCapture,
                        sourceFile = sourceFile,
                        sourceRank = sourceRank,
                        destFile = destFile,
                        destRank = nextChar.digitToInt()
                    )
                    else -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                }
            }
            nextChar.isPromotion -> when (piece) {
                PGNGamePiece.Pawn -> when (destFile) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> when (destRank) {
                        null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                        else -> continueWithPromotion(
                            startPosition = startPosition,
                            charsRead = charsRead + 1,
                            piece = piece,
                            isCapture = isCapture,
                            sourceFile = sourceFile,
                            sourceRank = sourceRank,
                            dest = PGNSquare(file = destFile, rank = destRank)
                        )

                    }
                }
                else -> throw PGNSANPieceDoesNotPromoteException(startPosition + charsRead, piece)
            }
            nextChar.isCheckStatus -> when (destFile) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (destRank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> handleCheckStatus(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        piece = piece,
                        promotionPiece = null,
                        castleType = null,
                        isCapture = isCapture,
                        sourceFile = sourceFile,
                        sourceRank = sourceRank,
                        dest = PGNSquare(file = destFile, rank = destRank),
                        checkStatus = PGNCheckStatus.fromChar(nextChar)
                    )
                }
            }
            nextChar.isSuffixAnnotationSymbol -> when (destFile) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (destRank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> continueWithSuffixAnnotationSymbol(
                        startPosition = startPosition,
                        charsRead = charsRead + 1,
                        castleType = null,
                        checkStatus = PGNCheckStatus.None,
                        piece = piece,
                        promotionPiece = null,
                        isCapture = isCapture,
                        sourceFile = sourceFile,
                        sourceRank = sourceRank,
                        dest = PGNSquare(file = destFile, rank = destRank),
                        suffixAnnotationSymbol = nextChar
                    )
                }
            }
            else -> when (destFile) {
                null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                else -> when (destRank) {
                    null -> throw PGNSANIllegalCharacterException(startPosition + charsRead, nextChar)
                    else -> PGNFSMResult(
                        charactersRead = charsRead,
                        value = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = destFile, rank = destRank),
                            piece = piece,
                            promotionPiece = null,
                            isCapture = isCapture,
                            sourceFileASCII = sourceFile?.code,
                            sourceRank = sourceRank,
                            suffixAnnotation = null
                        )
                    )
                }
            }
        }
    }

    private fun BufferedSource.continueWithPromotion(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        isCapture: Boolean,
        sourceFile: Char?,
        sourceRank: Int?,
        dest: PGNSquare
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            throw PGNParseException(startPosition + charsRead, "Unexpected end of file while reading PGN SAN move")
        }
        val nextChar = readUTF8Char()
        return when  {
            nextChar.isPossiblePromotionPiece -> continueWithCheckStatus(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                promotionPiece = PGNGamePiece.fromChar(nextChar),
                isCapture = isCapture,
                sourceFile = sourceFile,
                sourceRank = sourceRank,
                dest = dest
            )
            else -> PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = dest,
                    piece = piece,
                    promotionPiece = null,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
    }

    private fun BufferedSource.continueWithCheckStatus(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        promotionPiece: PGNGamePiece?,
        isCapture: Boolean,
        sourceFile: Char?,
        sourceRank: Int?,
        dest: PGNSquare
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            return PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isCheckStatus -> handleCheckStatus(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                piece = piece,
                promotionPiece = promotionPiece,
                castleType = null,
                isCapture = isCapture,
                sourceFile = sourceFile,
                sourceRank = sourceRank,
                dest = dest,
                checkStatus = PGNCheckStatus.fromChar(nextChar)
            )
            nextChar.isSuffixAnnotationSymbol -> continueWithSuffixAnnotationSymbol(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                castleType = null,
                checkStatus = PGNCheckStatus.None,
                piece = piece,
                promotionPiece = promotionPiece,
                isCapture = isCapture,
                sourceFile = sourceFile,
                sourceRank = sourceRank,
                dest = dest,
                suffixAnnotationSymbol = nextChar
            )
            else -> PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
    }

    private fun BufferedSource.handleCheckStatus(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        promotionPiece: PGNGamePiece?,
        castleType: PGNCastle?,
        isCapture: Boolean,
        sourceFile: Char?,
        sourceRank: Int?,
        dest: PGNSquare,
        checkStatus: PGNCheckStatus
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            return PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = checkStatus,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isSuffixAnnotationSymbol -> continueWithSuffixAnnotationSymbol(
                startPosition = startPosition,
                charsRead = charsRead + 1,
                castleType = castleType,
                checkStatus = checkStatus,
                piece = piece,
                promotionPiece = promotionPiece,
                isCapture = isCapture,
                sourceFile = sourceFile,
                sourceRank = sourceRank,
                dest = dest,
                suffixAnnotationSymbol = nextChar
            )
            else -> PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = checkStatus,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = null
                )
            )
        }
    }

    private fun BufferedSource.continueWithSuffixAnnotationSymbol(
        startPosition: Int,
        charsRead: Int,
        piece: PGNGamePiece,
        castleType: PGNCastle?,
        checkStatus: PGNCheckStatus,
        promotionPiece: PGNGamePiece?,
        isCapture: Boolean,
        sourceFile: Char?,
        sourceRank: Int?,
        dest: PGNSquare,
        suffixAnnotationSymbol: Char
    ): PGNParserResult<PGNSANMove> {
        if (exhausted()) {
            return PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = checkStatus,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = PGNSANMoveSuffixAnnotation.fromAnnotationText(suffixAnnotationSymbol.toString())
                )
            )
        }
        val nextChar = readUTF8Char()
        return when {
            nextChar.isSuffixAnnotationSymbol -> PGNFSMResult(
                charactersRead = charsRead + 1,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = checkStatus,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = PGNSANMoveSuffixAnnotation.fromAnnotationText("$suffixAnnotationSymbol$nextChar")
                )
            )
            else -> PGNFSMResult(
                charactersRead = charsRead,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = checkStatus,
                    destination = dest,
                    piece = piece,
                    promotionPiece = promotionPiece,
                    isCapture = isCapture,
                    sourceFileASCII = sourceFile?.code,
                    sourceRank = sourceRank,
                    suffixAnnotation = PGNSANMoveSuffixAnnotation.fromAnnotationText(suffixAnnotationSymbol.toString())
                )
            )
        }
    }

    private fun BufferedSource.castle(startPosition: Int, charsRead: Int): PGNParserResult<PGNSANMove> {
        var additionalCharsRead = 0
        var lastCharRead: Char = 'O'
        while (true) {
            val expectedNextChar = if (additionalCharsRead % 2 == 0) '-' else 'O'
            var mustBreak = false
            readExpectedUTF8Char(
                expected = expectedNextChar,
                onIOException = { ioe ->
                    when {
                        else -> when (additionalCharsRead) {
                            2, 4 -> mustBreak = true
                            else -> throw PGNParseException(
                                position = startPosition + charsRead + additionalCharsRead,
                                message = "Unexpected error while reading castle move",
                                cause = ioe
                            )
                        }
                    }
                },
                onDifferentCharRead = { char ->
                    when {
                        else -> when (additionalCharsRead) {
                            2, 4 -> {
                                lastCharRead = char
                                mustBreak = true
                            }
                            else -> throw PGNSANIllegalCharacterException(startPosition + charsRead + additionalCharsRead, lastCharRead)
                        }
                    }
                }
            )
            if (mustBreak) {
                break
            }
            additionalCharsRead++
        }
        val castleType = if (additionalCharsRead >= 4) PGNCastle.QueenSide else PGNCastle.KingSide
        return when {
            lastCharRead.isCheckStatus -> handleCheckStatus(
                startPosition = startPosition,
                charsRead = charsRead + additionalCharsRead + 1,
                piece = PGNGamePiece.King,
                promotionPiece = null,
                castleType = castleType,
                isCapture = false,
                sourceFile = null,
                sourceRank = null,
                dest = castleType.kingDestinationSquare(moveIsBlack),
                checkStatus = PGNCheckStatus.fromChar(lastCharRead),
            )
            lastCharRead.isSuffixAnnotationSymbol -> continueWithSuffixAnnotationSymbol(
                startPosition = startPosition,
                charsRead = charsRead + additionalCharsRead + 1,
                castleType = castleType,
                checkStatus = PGNCheckStatus.None,
                piece = PGNGamePiece.King,
                promotionPiece = null,
                isCapture = false,
                sourceFile = null,
                sourceRank = null,
                dest = castleType.kingDestinationSquare(moveIsBlack),
                suffixAnnotationSymbol = lastCharRead
            )
            else -> PGNFSMResult(
                charactersRead = charsRead + additionalCharsRead,
                value = PGNSANMove(
                    castleType = castleType,
                    checkStatus = PGNCheckStatus.None,
                    destination = castleType.kingDestinationSquare(moveIsBlack),
                    piece = PGNGamePiece.King,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
        }
    }
}