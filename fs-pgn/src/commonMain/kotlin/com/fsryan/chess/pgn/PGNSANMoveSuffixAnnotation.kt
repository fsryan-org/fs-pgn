@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNSANMoveSuffixAnnotation(val nagId: Int, val annotationText: String) {
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
        PGNSANMoveSuffixAnnotation.GoodMove.annotationText -> PGNSANMoveSuffixAnnotation.GoodMove
        PGNSANMoveSuffixAnnotation.PoorMove.annotationText -> PGNSANMoveSuffixAnnotation.PoorMove
        PGNSANMoveSuffixAnnotation.VeryGoodMove.annotationText -> PGNSANMoveSuffixAnnotation.VeryGoodMove
        PGNSANMoveSuffixAnnotation.VeryPoorMove.annotationText -> PGNSANMoveSuffixAnnotation.VeryPoorMove
        PGNSANMoveSuffixAnnotation.SpeculativeMove.annotationText -> PGNSANMoveSuffixAnnotation.SpeculativeMove
        PGNSANMoveSuffixAnnotation.QuestionableMove.annotationText -> PGNSANMoveSuffixAnnotation.QuestionableMove
        else -> throw IllegalArgumentException("No PGNPlySuffixAnnotation found for annotationText: $annotationText")
    }
}