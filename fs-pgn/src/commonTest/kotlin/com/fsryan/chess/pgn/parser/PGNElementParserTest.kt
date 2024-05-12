package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.PGNSANMove
import com.fsryan.chess.pgn.PGNSquare
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PGNElementParserTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReachedWhileReading() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = true)).parse(buf, 0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading PGN element", e.message)
            }
        }
    }

    @Test
    fun shouldParseSANOnly() {
        Buffer().use { buf ->
            buf.write("e4".encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = true,
                numberIndicator = null,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = true)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMoveIndicatorAndSAN() {
        Buffer().use { buf ->
            buf.write("1... e5".encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = true,
                numberIndicator = 1,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 5),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = true)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals("1... e5".length, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMoveIndicatorAndSANAndNAG() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1... e5 \$${randomNAG.id}"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = true,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 5),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = true)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldParseOnlyFirstElement() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1... e4 \$${randomNAG.id} e5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2 /* we read the whitespace, but not the next element */, result.charactersRead)
        }
    }

    @Test
    fun shouldParseRecursiveAnnotationVariation() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id} (1. Nf3)"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = PGNRecursiveVariationAnnotation(
                    plies = listOf(
                        PGNGamePly(
                            commentsArray = emptyArray(),
                            isBlack = false,
                            numberIndicator = 1,
                            numericAnnotationGlyph = null,
                            recursiveAnnotationVariation = null,
                            sanMove = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = 'f', rank = 3),
                                piece = PGNGamePiece.Knight,
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                        )
                    )
                ),
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldParseNestedVariations() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id} (1. Nf3 (1. Nc3 (1. Nh3)))"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = emptyArray(),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = PGNRecursiveVariationAnnotation(
                    plies = listOf(
                        PGNGamePly(
                            commentsArray = emptyArray(),
                            isBlack = false,
                            numberIndicator = 1,
                            numericAnnotationGlyph = null,
                            recursiveAnnotationVariation = PGNRecursiveVariationAnnotation(
                                plies = listOf(
                                    PGNGamePly(
                                        commentsArray = emptyArray(),
                                        isBlack = false,
                                        numberIndicator = 1,
                                        numericAnnotationGlyph = null,
                                        recursiveAnnotationVariation = PGNRecursiveVariationAnnotation(
                                            plies = listOf(
                                                PGNGamePly(
                                                    commentsArray = emptyArray(),
                                                    isBlack = false,
                                                    numberIndicator = 1,
                                                    numericAnnotationGlyph = null,
                                                    recursiveAnnotationVariation = null,
                                                    sanMove = PGNSANMove(
                                                        castleType = null,
                                                        checkStatus = PGNCheckStatus.None,
                                                        destination = PGNSquare(file = 'h', rank = 3),
                                                        piece = PGNGamePiece.Knight,
                                                        promotionPiece = null,
                                                        isCapture = false,
                                                        sourceFileASCII = null,
                                                        sourceRank = null,
                                                        suffixAnnotation = null
                                                    )
                                                )
                                            )
                                        ),
                                        sanMove = PGNSANMove(
                                            castleType = null,
                                            checkStatus = PGNCheckStatus.None,
                                            destination = PGNSquare(file = 'c', rank = 3),
                                            piece = PGNGamePiece.Knight,
                                            promotionPiece = null,
                                            isCapture = false,
                                            sourceFileASCII = null,
                                            sourceRank = null,
                                            suffixAnnotation = null
                                        )
                                    )
                                )
                            ),
                            sanMove = PGNSANMove(
                                castleType = null,
                                checkStatus = PGNCheckStatus.None,
                                destination = PGNSquare(file = 'f', rank = 3),
                                piece = PGNGamePiece.Knight,
                                promotionPiece = null,
                                isCapture = false,
                                sourceFileASCII = null,
                                sourceRank = null,
                                suffixAnnotation = null
                            )
                        )
                    )
                ),
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldParseCommentaryWithCurlyBraces() {
        Buffer().use { buf ->
            val input = "1. e4 {This is a comment} e5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMultipleCommentariesWithCurlyBraces() {
        Buffer().use { buf ->
            val input = "1. e4 {This is a comment} {This is another comment} e5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                comments = listOf("This is a comment", "This is another comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseCommentaryWithCurlyBracesAfterNAG() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id} {This is a comment} e5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMultipleCommentsWithCurlyBracesAfterNAG() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id} {This is a comment} {This is another comment} e5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                comments = listOf("This is a comment", "This is another comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseEndOfLineCommentaryAfterMove() {
        Buffer().use { buf ->
            val input = "1. e4;This is a comment\ne5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMultipleEndOfLineCommentsAfterMove() {
        Buffer().use { buf ->
            val input = "1. e4;This is a comment\n;This is another comment\ne5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment", "This is another comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = null,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseEndOfLineCommentaryAfterNAG() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id};This is a comment\ne5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }

    @Test
    fun shouldParseMultipleEndOfLineCommentsAfterNAG() {
        Buffer().use { buf ->
            val randomNAG = PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.random()
            val input = "1. e4 \$${randomNAG.id};This is a comment\n;This is another comment\ne5"
            buf.write(input.encodeUtf8())
            val expected = PGNGamePly(
                commentsArray = arrayOf("This is a comment", "This is another comment"),
                isBlack = false,
                numberIndicator = 1,
                numericAnnotationGlyph = randomNAG,
                recursiveAnnotationVariation = null,
                sanMove = PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                )
            )
            val result = PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = false)).parse(buf, 0)
            assertEquals(expected, result.value)
            assertEquals(input.length - 2, result.charactersRead)
        }
    }
}