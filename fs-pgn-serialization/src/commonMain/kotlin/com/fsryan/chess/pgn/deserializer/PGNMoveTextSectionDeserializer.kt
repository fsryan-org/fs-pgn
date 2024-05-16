package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNMoveSectionElement
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.use

internal fun BufferedSource.deserializeMoveTextSection(
    position: Int
): PGNDeserializationResult<List<PGNMoveSectionElement>> {
    var moveIsBlack = false
    var nextPosition = position
    val elements = mutableListOf<PGNMoveSectionElement>()
    while (true) {
        if (exhausted()) {
            break
        }

        nextPosition += readWhitespace(nextPosition)

        var readNextMove = true
        peek().use { peekable ->
            try {
                val terminator = peekable.deserializeGameTermination(nextPosition)
                nextPosition += terminator.charactersRead
                elements.add(terminator.value)
                incrementByUTF8CharacterCount(terminator.charactersRead)
                return PGNDeserializationResult(value = elements, charactersRead = nextPosition - position)
            } catch (e: PGNParseException) {
                // do nothing
                readNextMove = true
            }
        }

        if (!readNextMove) {
            break
        }

        val result = deserializePGNElement(nextPosition, moveIsBlack = moveIsBlack)
        elements.add(result.value)
        nextPosition += result.charactersRead
        moveIsBlack = !moveIsBlack
    }

    return PGNDeserializationResult(value = elements, charactersRead = nextPosition - position)
}