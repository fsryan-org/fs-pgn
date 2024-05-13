@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
enum class PGNGameTermination(val serialValue: String) {
    /**
     * abandoned game.
     */
    Abandoned("abandoned"),
    /**
     * result due to third party adjudication process.
     */
    Adjudication("adjudication"),
    /**
     * losing player called to greater things, one hopes.
     */
    Death("death"),
    /**
     * game concluded due to unforeseen circumstances.
     */
    Emergency("emergency"),
    /**
     * game terminated in a normal fashion.
     */
    Normal("normal"),
    /**
     * administrative forfeit due to losing player's failure to observe either the Laws of Chess or the event regulations.
     */
    RulesInfraction("rules infraction"),
    /**
     * loss due to losing player's failure to meet time control requirements.
     */
    TimeForfeit("time forfeit"),
    /**
     * game not terminated.
     */
    Unterminated("unterminated");

    companion object {
        fun fromSerialValue(serialValue: String): PGNGameTermination? {
            return entries.firstOrNull { it.serialValue == serialValue }
        }
    }
}