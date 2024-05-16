@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface PGNGame {
    val tags: PGNGameTags
    val elementsArray: Array<PGNMoveSectionElement>
}

@JsExport
@JsName("createPGNGame")
fun PGNGame(tags: PGNGameTags, elementsArray: Array<PGNMoveSectionElement>): PGNGame = PGNGame(tags, elementsArray.toList())

fun PGNGame(tags: PGNGameTags, elements: List<PGNMoveSectionElement>): PGNGame = PGNGameData(tags, elements)


val PGNGame.elements: List<PGNMoveSectionElement>
    get() = when (this) {
        is PGNGameData -> _elements
        else -> elementsArray.toList()
    }

private data class PGNGameData(
    override val tags: PGNGameTags,
    internal val _elements: List<PGNMoveSectionElement>
): PGNGame {
    override val elementsArray: Array<PGNMoveSectionElement>
        get() = _elements.toTypedArray()
}