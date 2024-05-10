@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface PGNSANMove {
    val castleType: PGNCastle?
    val checkStatus: PGNCheckStatus
    val destination: PGNSquare
    val isCapture: Boolean
    val piece: PGNGamePiece
    val promotionPiece: PGNGamePiece?
    val sourceFileASCII: Int?
    val sourceRank: Int?
    val suffixAnnotation: PGNSANMoveSuffixAnnotation?
}

val PGNSANMove.sourceFile: Char?
    get() = when (this) {
        is PGNSANMoveData -> _sourceFile
        else -> sourceFileASCII?.toChar()
    }

val PGNSANMove.sourceSquare: PGNSquare?
    get() = sourceFile?.let { sourceFile ->
        sourceRank?.let { sourceRank ->
            PGNSquare(file = sourceFile, rank = sourceRank)
        }
    }

@JsExport
@JsName("createPGNSANMove")
fun PGNSANMove(
    castleType: PGNCastle?,
    checkStatus: PGNCheckStatus,
    destination: PGNSquare,
    piece: PGNGamePiece,
    promotionPiece: PGNGamePiece?,
    isCapture: Boolean,
    sourceFileASCII: Int?,
    sourceRank: Int?,
    suffixAnnotation: PGNSANMoveSuffixAnnotation?
): PGNSANMove {
    return PGNSANMoveData(
        castleType = castleType,
        checkStatus = checkStatus,
        destination = destination,
        isCapture = isCapture,
        piece = piece,
        promotionPiece = promotionPiece,
        _sourceFile = sourceFileASCII?.toChar(),
        sourceRank = sourceRank,
        suffixAnnotation = suffixAnnotation
    )
}

@JsExport
enum class PGNCheckStatus(val serialValue: String) {
    None(""),
    Check("+"),
    Checkmate("#");

    companion object {
        fun fromSerialValue(serialValue: String): PGNCheckStatus {
            return when (serialValue) {
                None.serialValue -> None
                Check.serialValue -> Check
                Checkmate.serialValue -> Checkmate
                else -> throw IllegalArgumentException("No PGNCheckStatus found for serialValue: $serialValue")
            }
        }
    }
}

fun PGNCheckStatus.Companion.fromChar(char: Char): PGNCheckStatus {
    return when (char) {
        '+' -> PGNCheckStatus.Check
        '#' -> PGNCheckStatus.Checkmate
        else -> PGNCheckStatus.None
    }
}

private data class PGNSANMoveData(
    override val castleType: PGNCastle?,
    override val checkStatus: PGNCheckStatus,
    override val destination: PGNSquare,
    override val isCapture: Boolean,
    override val piece: PGNGamePiece,
    override val promotionPiece: PGNGamePiece?,
    internal val _sourceFile: Char?,
    override val sourceRank: Int?,
    override val suffixAnnotation: PGNSANMoveSuffixAnnotation?
) : PGNSANMove {
    override val sourceFileASCII: Int?
        get() = _sourceFile?.code
}