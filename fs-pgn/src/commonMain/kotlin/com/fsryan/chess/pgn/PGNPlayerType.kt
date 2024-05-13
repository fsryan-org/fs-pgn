@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNPlayerType(val serialValue: String) {
    Computer("program"), Human("human"), Unknown("unknown");

    companion object {
        fun fromSerialValue(playerType: String): PGNPlayerType {
            return when (playerType) {
                Computer.serialValue -> Computer
                Human.serialValue -> Human
                else -> Unknown
            }
        }
    }
}