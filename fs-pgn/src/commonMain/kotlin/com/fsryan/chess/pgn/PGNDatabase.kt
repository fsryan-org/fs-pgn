@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNDatabase {
    /**
     * All games in the standard collating sequence
     */
    val all: Array<PGNGame>
}