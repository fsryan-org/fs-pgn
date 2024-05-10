@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNRecursiveVariationAnnotation {
    val pliesArray: Array<PGNGamePly>
}

fun PGNRecursiveVariationAnnotation(plies: List<PGNGamePly>): PGNRecursiveVariationAnnotation {
    return PGNRecursiveVariationAnnotationData(plies)
}

private data class PGNRecursiveVariationAnnotationData(
    private val _plies: List<PGNGamePly>
): PGNRecursiveVariationAnnotation {
    override val pliesArray: Array<PGNGamePly>
        get() = _plies.toTypedArray()
}