package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNSANMoveSuffixAnnotation
import com.fsryan.chess.pgn.PGNSANMove
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.charValue
import com.fsryan.chess.pgn.fromChar
import com.fsryan.chess.pgn.kingDestinationSquare
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PGNSANMoveParserTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReached() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading PGN SAN move", e.message)
            }
        }
    }

    @Test
    fun shouldCorrectlyParseTwoLetterMoves() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(2, 3, 4, 5, 6, 7).forEach { rank -> // <-- non-promotion ranks
                Buffer().use { buf ->
                    buf.write("$file$rank ".encodeUtf8())
                    val expected = PGNSANMove(
                        castleType = null,
                        checkStatus = PGNCheckStatus.None,
                        destination = PGNSquare(file = file, rank = rank),
                        piece = PGNGamePiece.Pawn,
                        promotionPiece = null,
                        isCapture = false,
                        sourceFileASCII = null,
                        sourceRank = null,
                        suffixAnnotation = null
                    )
                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                    assertEquals(expected, result.value)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseTwoLetterMovesWithSuffixAnnotation() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(2, 3, 4, 5, 6, 7).forEach { rank -> // <-- non-promotion ranks
                PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                    Buffer().use { buf ->
                        buf.write("$file$rank${suffixAnnotation.annotationText} ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = file, rank = rank),
                            piece = PGNGamePiece.Pawn,
                            promotionPiece = null,
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = suffixAnnotation
                        )
                        val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCheckStatus() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(2, 3, 4, 5, 6, 7).forEach { rank -> // <-- non-promotion ranks
                sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                    Buffer().use { buf ->
                        buf.write("$file$rank$checkChar ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = expectedStatus,
                            destination = PGNSquare(file = file, rank = rank),
                            piece = PGNGamePiece.Pawn,
                            promotionPiece = null,
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(2, 3, 4, 5, 6, 7).forEach { rank -> // <-- non-promotion ranks
                sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("$file$rank$checkChar${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = expectedStatus,
                                destination = PGNSquare(file = file, rank = rank),
                                piece = PGNGamePiece.Pawn,
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithPromotion() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(1, 8).forEach { rank -> // <-- promotion ranks
                sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                    Buffer().use { buf ->
                        buf.write("$file$rank=$promotionPieceChar ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = file, rank = rank),
                            piece = PGNGamePiece.Pawn,
                            promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = rank == 1).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithPromotionWithSuffixAnnotation() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(1, 8).forEach { rank -> // <-- promotion ranks
                sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("$file$rank=$promotionPieceChar${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = file, rank = rank),
                                piece = PGNGamePiece.Pawn,
                                promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = rank == 1).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithPromotionWithCheckStatus() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(1, 8).forEach { rank -> // <-- promotion ranks
                sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                    sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                        Buffer().use { buf ->
                            buf.write("$file$rank=$promotionPieceChar$checkChar ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = expectedStatus,
                                destination = PGNSquare(file = file, rank = rank),
                                piece = PGNGamePiece.Pawn,
                                promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = rank == 1).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithPromotionWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
            sequenceOf(1, 8).forEach { rank -> // <-- promotion ranks
                sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                    sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("$file$rank=$promotionPieceChar$checkChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedStatus,
                                    destination = PGNSquare(file = file, rank = rank),
                                    piece = PGNGamePiece.Pawn,
                                    promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                                    isCapture = false,
                                    sourceFileASCII = null,
                                    sourceRank = null,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = rank == 1).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCapture() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(2, 3, 4, 5, 6, 7).forEach { destinationRank ->
                    Buffer().use { buf ->
                        buf.write("${sourceFile}x$destinationFile$destinationRank ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = destinationFile, rank = destinationRank),
                            piece = PGNGamePiece.Pawn,
                            promotionPiece = null,
                            isCapture = true,
                            sourceFileASCII = sourceFile.code,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCaptureWithSuffixAnnotation() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(2, 3, 4, 5, 6, 7).forEach { destinationRank ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("${sourceFile}x$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                piece = PGNGamePiece.Pawn,
                                promotionPiece = null,
                                isCapture = true,
                                sourceFileASCII = sourceFile.code,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCaptureWithCheckStatus() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(2, 3, 4, 5, 6, 7).forEach { destinationRank ->
                    sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                        Buffer().use { buf ->
                            buf.write("${sourceFile}x$destinationFile$destinationRank$checkChar ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = expectedStatus,
                                destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                piece = PGNGamePiece.Pawn,
                                promotionPiece = null,
                                isCapture = true,
                                sourceFileASCII = sourceFile.code,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCaptureWithCheckStatusWithSuffixAnnotation() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(2, 3, 4, 5, 6, 7).forEach { destinationRank ->
                    sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("${sourceFile}x$destinationFile$destinationRank$checkChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedStatus,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.Pawn,
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCaptureWithCheckStatusWithPromotion() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(1, 8).forEach { destinationRank ->
                    sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                            Buffer().use { buf ->
                                buf.write("${sourceFile}x$destinationFile$destinationRank=$promotionPieceChar$checkChar ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedStatus,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.Pawn,
                                    promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                                    isCapture = true,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = destinationRank == 1).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParsePawnMovesWithCaptureWithCheckStatusWithPromotionWithSuffixAnnotation() {
        sequenceOf(
            'a' to sequenceOf('b'),
            'b' to sequenceOf('a', 'c'),
            'c' to sequenceOf('b', 'd'),
            'd' to sequenceOf('c', 'e'),
            'e' to sequenceOf('d', 'f'),
            'f' to sequenceOf('e', 'g'),
            'g' to sequenceOf('f', 'h'),
            'h' to sequenceOf('g')
        ).forEach { (sourceFile, destinationFileList) ->
            destinationFileList.forEach { destinationFile ->
                sequenceOf(1, 8).forEach { destinationRank ->
                    sequenceOf('Q', 'R', 'B', 'N').forEach { promotionPieceChar ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("${sourceFile}x$destinationFile$destinationRank=$promotionPieceChar$checkChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = expectedStatus,
                                        destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                        piece = PGNGamePiece.Pawn,
                                        promotionPiece = PGNGamePiece.fromChar(promotionPieceChar),
                                        isCapture = true,
                                        sourceFileASCII = sourceFile.code,
                                        sourceRank = null,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = destinationRank == 1).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithPieceIndicator() {
        PGNGamePiece.entries.map { it.charValue }.forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
                sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                    pieceChar != 'P' || (it in 2..7)
                }.forEach { rank ->
                    Buffer().use { buf ->
                        buf.write("$pieceChar$file$rank ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = file, rank = rank),
                            piece = PGNGamePiece.fromChar(pieceChar),
                            promotionPiece = null,
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithPieceIndicatorWithSuffixAnnotation() {
        PGNGamePiece.entries.map { it.charValue }.forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { file ->
                sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                    pieceChar != 'P' || (it in 2..7)
                }.forEach { rank ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("$pieceChar$file$rank${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = file, rank = rank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguator() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        Buffer().use { buf ->
                            buf.write("$pieceChar$sourceFile$destinationFile$rank ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = rank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = sourceFile.code,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithSuffixAnntation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar$sourceFile$destinationFile$rank${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = rank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = false,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCheckStatus() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar$sourceFile$destinationFile$rank$checkStatusChar ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedCheckStatus,
                                    destination = PGNSquare(file = destinationFile, rank = rank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = false,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("$pieceChar$sourceFile$destinationFile$rank$checkStatusChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = expectedCheckStatus,
                                        destination = PGNSquare(file = destinationFile, rank = rank),
                                        piece = PGNGamePiece.fromChar(pieceChar),
                                        promotionPiece = null,
                                        isCapture = false,
                                        sourceFileASCII = sourceFile.code,
                                        sourceRank = null,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCapture() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        Buffer().use { buf ->
                            buf.write("$pieceChar${sourceFile}x$destinationFile$rank ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = rank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = true,
                                sourceFileASCII = sourceFile.code,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCaptureWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar${sourceFile}x$destinationFile$rank${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = rank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCaptureWithCheckStatus() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar${sourceFile}x$destinationFile$rank$checkStatusChar ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedCheckStatus,
                                    destination = PGNSquare(file = destinationFile, rank = rank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = null,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileOnlyDisambiguatorWithCaptureWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { rank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("$pieceChar${sourceFile}x$destinationFile$rank$checkStatusChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = expectedCheckStatus,
                                        destination = PGNSquare(file = destinationFile, rank = rank),
                                        piece = PGNGamePiece.fromChar(pieceChar),
                                        promotionPiece = null,
                                        isCapture = true,
                                        sourceFileASCII = sourceFile.code,
                                        sourceRank = null,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguator() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        Buffer().use { buf ->
                            buf.write("$pieceChar$sourceRank$destinationFile$destinationRank ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = sourceRank,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguatorWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar$sourceRank$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = false,
                                    sourceFileASCII = null,
                                    sourceRank = sourceRank,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguatorWithCapture() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        Buffer().use { buf ->
                            buf.write("$pieceChar${sourceRank}x$destinationFile$destinationRank ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = true,
                                sourceFileASCII = null,
                                sourceRank = sourceRank,
                                suffixAnnotation = null
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguatorWithCaptureWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar${sourceRank}x$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = null,
                                    sourceRank = sourceRank,
                                    suffixAnnotation = suffixAnnotation
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguatorWithCaptureWithCheckStatus() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar${sourceRank}x$destinationFile$destinationRank$checkStatusChar ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = expectedCheckStatus,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = null,
                                    sourceRank = sourceRank,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithRankOnlyDisambiguatorWithCaptureWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                        pieceChar != 'P' || (it in 2..7)
                    }.forEach { destinationRank ->
                        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkStatusChar, expectedCheckStatus) ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("$pieceChar${sourceRank}x$destinationFile$destinationRank$checkStatusChar${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = expectedCheckStatus,
                                        destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                        piece = PGNGamePiece.fromChar(pieceChar),
                                        promotionPiece = null,
                                        isCapture = true,
                                        sourceFileASCII = null,
                                        sourceRank = sourceRank,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileAndRankDisambiguator() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                    sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                        sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                            pieceChar != 'P' || (it in 2..7)
                        }.forEach { destinationRank ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar$sourceFile$sourceRank$destinationFile$destinationRank ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = false,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = sourceRank,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileAndRankDisambiguatorWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                    sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                        sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                            pieceChar != 'P' || (it in 2..7)
                        }.forEach { destinationRank ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("$pieceChar$sourceFile$sourceRank$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = PGNCheckStatus.None,
                                        destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                        piece = PGNGamePiece.fromChar(pieceChar),
                                        promotionPiece = null,
                                        isCapture = false,
                                        sourceFileASCII = sourceFile.code,
                                        sourceRank = sourceRank,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileAndRankDisambiguatorWithCapture() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                    sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                        sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                            pieceChar != 'P' || (it in 2..7)
                        }.forEach { destinationRank ->
                            Buffer().use { buf ->
                                buf.write("$pieceChar$sourceFile${sourceRank}x$destinationFile$destinationRank ".encodeUtf8())
                                val expected = PGNSANMove(
                                    castleType = null,
                                    checkStatus = PGNCheckStatus.None,
                                    destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                    piece = PGNGamePiece.fromChar(pieceChar),
                                    promotionPiece = null,
                                    isCapture = true,
                                    sourceFileASCII = sourceFile.code,
                                    sourceRank = sourceRank,
                                    suffixAnnotation = null
                                )
                                val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                assertEquals(expected, result.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseMoveWithFileAndRankDisambiguatorWithCaptureWithSuffixAnnotation() {
        sequenceOf('P', 'N', 'B', 'R', 'Q').forEach { pieceChar ->
            sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                pieceChar != 'P' || (it in 2..7)
            }.forEach { sourceRank ->
                sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { sourceFile ->
                    sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                        sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).filter {
                            pieceChar != 'P' || (it in 2..7)
                        }.forEach { destinationRank ->
                            PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                                Buffer().use { buf ->
                                    buf.write("$pieceChar$sourceFile${sourceRank}x$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                                    val expected = PGNSANMove(
                                        castleType = null,
                                        checkStatus = PGNCheckStatus.None,
                                        destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                        piece = PGNGamePiece.fromChar(pieceChar),
                                        promotionPiece = null,
                                        isCapture = true,
                                        sourceFileASCII = sourceFile.code,
                                        sourceRank = sourceRank,
                                        suffixAnnotation = suffixAnnotation
                                    )
                                    val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                                    assertEquals(expected, result.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseNonPawnMoveWithCapture() {
        PGNGamePiece.entries.map { it.charValue }.forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).forEach { destinationRank ->
                    Buffer().use { buf ->
                        buf.write("${pieceChar}x$destinationFile$destinationRank ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = null,
                            checkStatus = PGNCheckStatus.None,
                            destination = PGNSquare(file = destinationFile, rank = destinationRank),
                            piece = PGNGamePiece.fromChar(pieceChar),
                            promotionPiece = null,
                            isCapture = true,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseNonPawnMoveWithCaptureWithSuffixAnnotation() {
        PGNGamePiece.entries.map { it.charValue }.forEach { pieceChar ->
            sequenceOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h').forEach { destinationFile ->
                sequenceOf(1, 2, 3, 4, 5, 6, 7, 8).forEach { destinationRank ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("${pieceChar}x$destinationFile$destinationRank${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = destinationFile, rank = destinationRank),
                                piece = PGNGamePiece.fromChar(pieceChar),
                                promotionPiece = null,
                                isCapture = true,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldParseCastlingKingSideAsBlack() {
        Buffer().use { buf ->
            buf.write("O-O ".encodeUtf8())
            val expected = PGNSANMove(
                castleType = PGNCastle.KingSide,
                checkStatus = PGNCheckStatus.None,
                destination = PGNSquare(file = 'g', rank = 8),
                piece = PGNGamePiece.King,
                promotionPiece = null,
                isCapture = false,
                sourceFileASCII = null,
                sourceRank = null,
                suffixAnnotation = null
            )
            val result = PGNSANMoveParser(moveIsBlack = true).parse(buf, 0)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldParseCastlingQueenSideAsBlack() {
        Buffer().use { buf ->
            buf.write("O-O-O ".encodeUtf8())
            val expected = PGNSANMove(
                castleType = PGNCastle.QueenSide,
                checkStatus = PGNCheckStatus.None,
                destination = PGNSquare(file = 'c', rank = 8),
                piece = PGNGamePiece.King,
                promotionPiece = null,
                isCapture = false,
                sourceFileASCII = null,
                sourceRank = null,
                suffixAnnotation = null
            )
            val result = PGNSANMoveParser(moveIsBlack = true).parse(buf, 0)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldParseCastlingKingSideAsWhite() {
        Buffer().use { buf ->
            buf.write("O-O ".encodeUtf8())
            val expected = PGNSANMove(
                castleType = PGNCastle.KingSide,
                checkStatus = PGNCheckStatus.None,
                destination = PGNSquare(file = 'g', rank = 1),
                piece = PGNGamePiece.King,
                promotionPiece = null,
                isCapture = false,
                sourceFileASCII = null,
                sourceRank = null,
                suffixAnnotation = null
            )
            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldParseCastlingQueenSideAsWhite() {
        Buffer().use { buf ->
            buf.write("O-O-O ".encodeUtf8())
            val expected = PGNSANMove(
                castleType = PGNCastle.QueenSide,
                checkStatus = PGNCheckStatus.None,
                destination = PGNSquare(file = 'c', rank = 1),
                piece = PGNGamePiece.King,
                promotionPiece = null,
                isCapture = false,
                sourceFileASCII = null,
                sourceRank = null,
                suffixAnnotation = null
            )
            val result = PGNSANMoveParser(moveIsBlack = false).parse(buf, 0)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldParseCastlingWithCheckStatus() {
        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
            sequenceOf("O-O" to PGNCastle.KingSide, "O-O-O" to PGNCastle.QueenSide).forEach { (serializedCastle, castleType) ->
                sequenceOf(true, false).forEach { isBlack ->
                    Buffer().use { buf ->
                        buf.write("${serializedCastle}$checkChar ".encodeUtf8())
                        val expected = PGNSANMove(
                            castleType = castleType,
                            checkStatus = expectedStatus,
                            destination = castleType.kingDestinationSquare(isBlack),
                            piece = PGNGamePiece.King,
                            promotionPiece = null,
                            isCapture = false,
                            sourceFileASCII = null,
                            sourceRank = null,
                            suffixAnnotation = null
                        )
                        val result = PGNSANMoveParser(moveIsBlack = isBlack).parse(buf, 0)
                        assertEquals(expected, result.value)
                    }
                }
            }
        }
    }

    @Test
    fun shouldParseCastlingWithCheckStatusWithSuffixAnnotation() {
        sequenceOf('+' to PGNCheckStatus.Check, '#' to PGNCheckStatus.Checkmate).forEach { (checkChar, expectedStatus) ->
            sequenceOf("O-O" to PGNCastle.KingSide, "O-O-O" to PGNCastle.QueenSide).forEach { (serializedCastle, castleType) ->
                sequenceOf(true, false).forEach { isBlack ->
                    PGNSANMoveSuffixAnnotation.entries.forEach { suffixAnnotation ->
                        Buffer().use { buf ->
                            buf.write("${serializedCastle}$checkChar${suffixAnnotation.annotationText} ".encodeUtf8())
                            val expected = PGNSANMove(
                                castleType = castleType,
                                checkStatus = expectedStatus,
                                destination = castleType.kingDestinationSquare(isBlack),
                                piece = PGNGamePiece.King,
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = suffixAnnotation
                            )
                            val result = PGNSANMoveParser(moveIsBlack = isBlack).parse(buf, 0)
                            assertEquals(expected, result.value)
                        }
                    }
                }
            }
        }
    }
}