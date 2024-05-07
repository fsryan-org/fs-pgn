@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNGameResult(val serialValue: String) {
    BlackWins("0-1"),
    Draw("1/2-1/2"),
    InProgressAbandonedOrUnknown("*"),
    WhiteWins("1-0");

    companion object {
        fun fromSerialValue(serialValue: String): PGNGameResult {
            return when (serialValue) {
                BlackWins.serialValue -> BlackWins
                Draw.serialValue -> Draw
                InProgressAbandonedOrUnknown.serialValue -> InProgressAbandonedOrUnknown
                WhiteWins.serialValue -> WhiteWins
                else -> throw IllegalArgumentException("Unknown PGNGameResult serial value: $serialValue")
            }
        }
    }
}