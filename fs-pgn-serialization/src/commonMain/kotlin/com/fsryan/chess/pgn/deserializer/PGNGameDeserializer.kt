package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameTags
import okio.BufferedSource

internal fun BufferedSource.deserializePGNGame(position: Int): PGNDeserializationResult<PGNGame> {
    if (exhausted()) {
        return PGNDeserializationResult(value = PGNGame(PGNGameTags(emptyMap()), emptyList()), charactersRead = 0)
    }

    var nextPosition = position + readWhitespace(position)
    val tagResult = deserializeGameTags(nextPosition)
    nextPosition += tagResult.charactersRead + readWhitespace(nextPosition)
    val moveTextResult = deserializeMoveTextSection(nextPosition)
    return PGNDeserializationResult(
        value = PGNGame(tagResult.value, moveTextResult.value),
        charactersRead = nextPosition - position + moveTextResult.charactersRead
    )
}