@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNGame {
    val tags: PGNGameTags
//    val moves: List<PGNGameMove>
}