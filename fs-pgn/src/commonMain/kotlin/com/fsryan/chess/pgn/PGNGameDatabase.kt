@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNGameDatabase {
    /**
     * All games in the standard collating sequence
     */
    val allArray: Array<PGNGame>
}

fun PGNGameDatabase(all: List<PGNGame>): PGNGameDatabase = PGNGameDatabaseData(all)

@JsExport
fun createPGNGameDatabase(allArray: Array<PGNGame>): PGNGameDatabase = PGNGameDatabase(allArray.toList())

val PGNGameDatabase.all: List<PGNGame>
    get() = when (this) {
        is PGNGameDatabaseData -> _all
        else -> allArray.toList()
    }

// Do indexing here
private data class PGNGameDatabaseData(internal val _all: List<PGNGame>): PGNGameDatabase {
    override val allArray: Array<PGNGame>
        get() = _all.toTypedArray()
}