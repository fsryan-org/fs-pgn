package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNMoveSectionElement
import okio.BufferedSource
import okio.use

internal fun BufferedSource.deserializeMoveTextSection(
    position: Int
): PGNDeserializationResult<List<PGNMoveSectionElement>> {
    var moveIsBlack = false
    var nextPosition = position
    val elements = mutableListOf<PGNMoveSectionElement>()
    while (true) {
        nextPosition += readWhitespace(nextPosition)
        if (exhausted()) {
            break
        }

        var readNextMove = false
        peek().use { peekable ->
            val peekNextChar = peekable.readUTF8Char()
            when {
                peekNextChar == '0' || peekNextChar == '*' -> {
                    val result = deserializePGNGameResult(nextPosition)
                    nextPosition += result.charactersRead
                    elements.add(result.value)
                }
                peekNextChar == '1' -> when (val nextNextChar = peekable.readUTF8Char()) {
                    '-', '/' -> {
                        val result = deserializePGNGameResult(nextPosition)
                        nextPosition += result.charactersRead
                        elements.add(result.value)
                    }
                    else -> readNextMove = true
                }
                peekNextChar.isDigit() || peekNextChar.canStartMove -> readNextMove = true
                else -> readNextMove = false
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