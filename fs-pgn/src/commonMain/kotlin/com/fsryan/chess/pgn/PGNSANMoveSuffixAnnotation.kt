@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNSANMoveSuffixAnnotation(val nagId: Int, val serialValue: String) {
    GoodMove(1, "!"),
    PoorMove(2, "?"),
    VeryGoodMove(3, "!!"),
    VeryPoorMove(4, "??"),
    SpeculativeMove(5, "!?"),
    QuestionableMove(6, "?!");

    companion object {
        fun fromNagId(nagId: Int): PGNSANMoveSuffixAnnotation {
            return entries.first { it.nagId == nagId }
        }
    }
}

fun PGNSANMoveSuffixAnnotation.Companion.fromAnnotationText(annotationText: String): PGNSANMoveSuffixAnnotation {
    return when (annotationText) {
        PGNSANMoveSuffixAnnotation.GoodMove.serialValue -> PGNSANMoveSuffixAnnotation.GoodMove
        PGNSANMoveSuffixAnnotation.PoorMove.serialValue -> PGNSANMoveSuffixAnnotation.PoorMove
        PGNSANMoveSuffixAnnotation.VeryGoodMove.serialValue -> PGNSANMoveSuffixAnnotation.VeryGoodMove
        PGNSANMoveSuffixAnnotation.VeryPoorMove.serialValue -> PGNSANMoveSuffixAnnotation.VeryPoorMove
        PGNSANMoveSuffixAnnotation.SpeculativeMove.serialValue -> PGNSANMoveSuffixAnnotation.SpeculativeMove
        PGNSANMoveSuffixAnnotation.QuestionableMove.serialValue -> PGNSANMoveSuffixAnnotation.QuestionableMove
        else -> throw IllegalArgumentException("No PGNPlySuffixAnnotation found for annotationText: $annotationText")
    }
}