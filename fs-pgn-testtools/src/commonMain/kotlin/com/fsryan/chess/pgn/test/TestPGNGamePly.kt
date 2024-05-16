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
import com.fsryan.chess.pgn.file
import com.fsryan.chess.pgn.kingDestinationFile
import kotlin.random.Random

fun TestSimplePly(
    comments: List<String> = emptyList(),
    isBlack: Boolean = Random.nextBoolean(),
    numberIndicator: Int?,
    numericAnnotationGlyph: PGNNumericAnnotationGlyph? = null,
    recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = null,
    sanMove: PGNSANMove = TestSimplePGNSANMove(isBlack = isBlack)
): PGNGamePly = PGNGamePly(
    comments = comments,
    isBlack = isBlack,
    numberIndicator = numberIndicator,
    numericAnnotationGlyph = numericAnnotationGlyph,
    recursiveAnnotationVariation = recursiveAnnotationVariation,
    sanMove = sanMove
)

fun TestPGNGamePly(
    moveNumber: Int,
    comments: List<String> = randomOptional(0.9F) { testListOf(maxSize = 3) { randomUUIDString() } }.orEmpty(),
    isBlack: Boolean = Random.nextBoolean(),
    numberIndicator: Int?,
    numericAnnotationGlyph: PGNNumericAnnotationGlyph? = randomOptional { randomEnumValue<PGNNumericAnnotationGlyph> { it != PGNNumericAnnotationGlyph.Unknown} },
    recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = randomOptional(0.9F) {
        TestPGNRecursiveVariationAnnotation(firstMoveIsBlack = isBlack, firstMoveNumber = moveNumber)
    },
    sanMove: PGNSANMove = TestPGNSANMove(isBlack = isBlack, suffixAnnotation = null)
): PGNGamePly = PGNGamePly(
    comments = comments,
    isBlack = isBlack,
    numberIndicator = numberIndicator,
    numericAnnotationGlyph = numericAnnotationGlyph,
    recursiveAnnotationVariation = recursiveAnnotationVariation,
    sanMove = sanMove
)

fun TestSimplePGNSANMove(
    isBlack: Boolean = Random.nextBoolean(),
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
    isBlack = isBlack,
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
    isBlack: Boolean = Random.nextBoolean(),
    castleType: PGNCastle? = randomOptional(0.96F) { randomEnumValue<PGNCastle>() },
    checkStatus: PGNCheckStatus = randomOptional(0.9F) {
        randomEnumValue<PGNCheckStatus> { it != PGNCheckStatus.None }
    } ?: PGNCheckStatus.None,
    destination: PGNSquare = when (castleType) {
        null -> TestPGNSquare()
        else -> PGNSquare(file = castleType.kingDestinationFile, rank = if (isBlack) 8 else 1)
    },
    piece: PGNGamePiece = castleType?.let { PGNGamePiece.King } ?: randomEnumValue<PGNGamePiece>(),
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
    isCapture: Boolean = castleType == null && Random.nextFloat() < 0.15F,
    sourceFile: Char? = when (castleType) {
        null -> when (isCapture) {
            true -> when (piece) {
                PGNGamePiece.Pawn -> when (destination.file) {
                    'a' -> 'b'
                    'h' -> 'g'
                    else -> destination.file + if (Random.nextBoolean()) -1 else 1
                }
                else -> randomOptional(0.9F) { ('a' .. 'h').random() }
            }
            false -> randomOptional(0.9F) { ('a' .. 'h').random() }
        }
        else -> null
    },  // TODO: make this more realistic
    sourceRank: Int? = when (castleType) {
        null -> when (piece) {
            PGNGamePiece.Pawn -> null
            else -> randomOptional(0.9F) { Random.nextInt(1, 9) }
        }  // TODO: make this more realistic
        else -> null
    },
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
    firstMoveNumber: Int,
    firstMoveIsBlack: Boolean,
    plies: List<PGNGamePly> = testListOf(maxSize = 8) { index ->
        val isBlack = index % 2 == 0 && firstMoveIsBlack || index % 2 == 1 && !firstMoveIsBlack
        val moveNumber = firstMoveNumber + if (firstMoveIsBlack) (index + 1) / 2 else index / 2
        val showNumberIndicator = index == 0 || !isBlack
        TestPGNGamePly(
            isBlack = isBlack,
            moveNumber = moveNumber,
            numberIndicator = if (showNumberIndicator) moveNumber else null
        )
    }
): PGNRecursiveVariationAnnotation = PGNRecursiveVariationAnnotation(plies = plies)