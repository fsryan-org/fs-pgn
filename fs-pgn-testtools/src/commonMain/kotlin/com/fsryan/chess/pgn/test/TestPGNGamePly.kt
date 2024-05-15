package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.PGNSANMove
import com.fsryan.chess.pgn.PGNSANMoveSuffixAnnotation
import com.fsryan.chess.pgn.PGNSquare
import com.fsryan.chess.pgn.kingDestinationFile
import kotlin.random.Random

fun TestSimplePly(
    comments: List<String> = emptyList(),
    isBlack: Boolean = Random.nextBoolean(),
    numberIndicator: Int? = if (isBlack) null else Random.nextInt(1, 100),
    numericAnnotationGlyph: PGNNumericAnnotationGlyph? = null,
    recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = null,
    sanMove: PGNSANMove = TestSimplePGNSANMove()
): PGNGamePly = PGNGamePly(
    comments = comments,
    isBlack = isBlack,
    numberIndicator = numberIndicator,
    numericAnnotationGlyph = numericAnnotationGlyph,
    recursiveAnnotationVariation = recursiveAnnotationVariation,
    sanMove = sanMove
)

fun TestPGNGamePly(
    comments: List<String> = randomOptional { testListOf(maxSize = 3) { randomUUIDString() } }.orEmpty(),
    isBlack: Boolean = Random.nextBoolean(),
    numberIndicator: Int? = randomOptional { Random.nextInt(1, 100) },
    numericAnnotationGlyph: PGNNumericAnnotationGlyph? = randomOptional { randomEnumValue<PGNNumericAnnotationGlyph>() },
    recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = randomOptional { TestPGNRecursiveVariationAnnotation() },
    sanMove: PGNSANMove = TestPGNSANMove()
): PGNGamePly = PGNGamePly(
    comments = comments,
    isBlack = isBlack,
    numberIndicator = numberIndicator,
    numericAnnotationGlyph = numericAnnotationGlyph,
    recursiveAnnotationVariation = recursiveAnnotationVariation,
    sanMove = sanMove
)

fun TestSimplePGNSANMove(
    castleType: PGNCastle? = null,
    checkStatus: PGNCheckStatus = PGNCheckStatus.None,
    destination: PGNSquare = TestPGNSquare(),
    piece: PGNGamePiece = when (destination.rank) {
        1, 8 -> randomEnumValue { it != PGNGamePiece.Pawn }
        else -> randomEnumValue()
    },
    promotionPiece: PGNGamePiece? = null,
    isCapture: Boolean = false,
    sourceFile: Char? = null,
    sourceRank: Int? = null,
    suffixAnnotation: PGNSANMoveSuffixAnnotation? = null
): PGNSANMove = TestPGNSANMove(
    castleType = castleType,
    checkStatus = checkStatus,
    destination = destination,
    piece = piece,
    promotionPiece = promotionPiece,
    isCapture = isCapture,
    sourceFile = sourceFile,
    sourceRank = sourceRank,
    suffixAnnotation = suffixAnnotation
)

fun TestPGNSANMove(
    castleType: PGNCastle? = randomOptional(0.96F) { randomEnumValue<PGNCastle>() },
    checkStatus: PGNCheckStatus = randomOptional(0.9F) {
        randomEnumValue<PGNCheckStatus> { it != PGNCheckStatus.None }
    } ?: PGNCheckStatus.None,
    destination: PGNSquare = when (castleType) {
        null -> TestPGNSquare()
        else -> PGNSquare(file = castleType.kingDestinationFile, rank = randomOptional { 1 } ?: 8)
    },
    piece: PGNGamePiece = randomEnumValue<PGNGamePiece>(),
    promotionPiece: PGNGamePiece? = when (piece) {
        PGNGamePiece.Pawn -> when (destination.rank) {
            1, 8 -> randomOptional(0.99F) { PGNGamePiece.Queen }
                ?: randomOptional(0.75F) { PGNGamePiece.Knight }
                ?: randomOptional(0.75F) { PGNGamePiece.Rook }
                ?: PGNGamePiece.Bishop
            else -> null
        }
        else -> null
    },
    isCapture: Boolean = Random.nextFloat() < 0.15F,
    sourceFile: Char? = randomOptional(0.9F) { ('a' .. 'h').random() },    // TODO: make this more realistic
    sourceRank: Int? = randomOptional(0.9F) { Random.nextInt(1, 9) },           // TODO: make this more realistic
    suffixAnnotation: PGNSANMoveSuffixAnnotation? = randomOptional(0.95F) { PGNSANMoveSuffixAnnotation.entries.random() }
): PGNSANMove = PGNSANMove(
    castleType = castleType,
    checkStatus = checkStatus,
    destination = destination,
    piece = piece,
    promotionPiece = promotionPiece,
    isCapture = isCapture,
    sourceFileASCII = sourceFile?.code,
    sourceRank = sourceRank,
    suffixAnnotation = suffixAnnotation
)


fun TestPGNRecursiveVariationAnnotation(
    plies: List<PGNGamePly> = testListOf(maxSize = 24) { index -> TestPGNGamePly(numberIndicator = index + 1) }
): PGNRecursiveVariationAnnotation = PGNRecursiveVariationAnnotation(plies = plies)