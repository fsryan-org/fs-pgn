package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource

fun BufferedSource.deserializePGNGameDatabase(position: Int = 0): PGNDeserializationResult<PGNGameDatabase> {
    val games = mutableListOf<PGNGame>()

    var nextPosition = position
    while (true) {
        nextPosition += readWhitespace(nextPosition)
        if (exhausted()) {
            return PGNDeserializationResult(0, PGNGameDatabase(games.toList()))
        }

        val gameResult = deserializePGNGame(nextPosition)
        games.add(gameResult.value)
        nextPosition += gameResult.charactersRead
    }
}