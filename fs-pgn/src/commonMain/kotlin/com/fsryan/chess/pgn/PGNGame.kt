@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
interface PGNGame {
    val tags: PGNGameTags
    val elements: List<PGNMoveSectionElement>
}

@JsExport
@JsName("createPGNGame")
fun PGNGame(tags: PGNGameTags, elements: List<PGNMoveSectionElement>): PGNGame = PGNGameData(tags, elements)

private class PGNGameData(
    override val tags: PGNGameTags,
    override val elements: List<PGNMoveSectionElement>
): PGNGame