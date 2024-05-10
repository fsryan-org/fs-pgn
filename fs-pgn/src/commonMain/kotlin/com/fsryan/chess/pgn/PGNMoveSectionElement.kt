@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * A marker interface for an element of a PGN Move section
 */
@JsExport
sealed interface PGNMoveSectionElement: PGNCommentable

/**
 * Represents a half-move (either black or white) in a chess game. A move
 * consists of two plies.
 */
@JsExport
interface PGNGamePly: PGNMoveSectionElement {
    val isBlack: Boolean
    val numberInditcator: Int?
    val numericAnnotationGlyph: PGNNumericAnnotationGlyph?
    val recursiveAnnotationVariation: PGNRecursiveVariationAnnotation?
    val sanMove: PGNSANMove
}

@JsExport
@JsName("createPGNGamePly")
fun PGNGamePly(
    commentsArray: Array<String>,
    isBlack: Boolean,
    numberIndicator: Int?,
    numericAnnotationGlyph: PGNNumericAnnotationGlyph?,
    recursiveAnnotationVariation: PGNRecursiveVariationAnnotation?,
    sanMove: PGNSANMove
): PGNGamePly = PGNGamePlyData(
    _comments = commentsArray.toList(),
    isBlack = isBlack,
    numberInditcator = numberIndicator,
    numericAnnotationGlyph = numericAnnotationGlyph,
    recursiveAnnotationVariation = recursiveAnnotationVariation,
    sanMove = sanMove
)

val PGNCommentable.comments: List<String>
    get() = when (this) {
        is PGNGamePlyData -> _comments
        is PGNGameResultData -> _comments
        else -> commentsArray.toList()
    }

@JsExport
interface PGNGameResult: PGNMoveSectionElement {
    val result: PGNGameResultValue
}

@JsExport
@JsName("createPGNGameResult")
fun PGNGameResult(value: PGNGameResultValue, comments: Array<String>): PGNGameResult {
    return PGNGameResult(value, comments.toList())
}
fun PGNGameResult(value: PGNGameResultValue, comments: List<String>): PGNGameResult {
    return PGNGameResultData(value, comments)
}

@JsExport
enum class PGNGameResultValue(val serialValue: String) {
    BlackWins("0-1"),
    Draw("1/2-1/2"),
    InProgressAbandonedOrUnknown("*"),
    WhiteWins("1-0");

    companion object {
        fun fromSerialValue(serialValue: String): PGNGameResultValue {
            return when (serialValue) {
                BlackWins.serialValue -> BlackWins
                Draw.serialValue -> Draw
                InProgressAbandonedOrUnknown.serialValue -> InProgressAbandonedOrUnknown
                WhiteWins.serialValue -> WhiteWins
                else -> throw IllegalArgumentException("Unknown PGNGameResult serial value: $serialValue")
            }
        }
    }
}

fun PGNGameResultValue.toPGNGameResult(comments: List<String> = emptyList()): PGNGameResult {
    return PGNGameResult(this, comments)
}

private data class PGNGameResultData(
    override val result: PGNGameResultValue,
    internal val _comments: List<String>
): PGNGameResult {
    override val commentsArray: Array<String>
        get() = _comments.toTypedArray()
}

private data class PGNGamePlyData(
    internal val _comments: List<String>,
    override val isBlack: Boolean,
    override val numberInditcator: Int?,
    override val numericAnnotationGlyph: PGNNumericAnnotationGlyph?,
    override val recursiveAnnotationVariation: PGNRecursiveVariationAnnotation?,
    override val sanMove: PGNSANMove
): PGNGamePly {
    override val commentsArray: Array<String>
        get() = _comments.toTypedArray()
}