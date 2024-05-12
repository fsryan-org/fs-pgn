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

val PGNRecursiveVariationAnnotation.plies: List<PGNGamePly>
    get() = when (this) {
        is PGNRecursiveVariationAnnotationData -> _plies
        else -> pliesArray.toList()
    }

private data class PGNRecursiveVariationAnnotationData(
    internal val _plies: List<PGNGamePly>
): PGNRecursiveVariationAnnotation {
    override val pliesArray: Array<PGNGamePly>
        get() = _plies.toTypedArray()
}