package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedTagTerminator
import com.fsryan.chess.pgn.PGNUnexpectedTagValueDelimiter
import okio.BufferedSource
import okio.use

internal fun BufferedSource.deserializeGameTags(position: Int): PGNDeserializationResult<PGNGameTags> {
    val tagPairs = mutableMapOf<String, String>()
    var currentPosition = position

    peek().use { peekable ->
        while (true) {
            var mustBreak = false
            currentPosition += peekable.readWhitespace(position)
            if (exhausted()) {
                break
            }

            peekable.readExpectedUTF8Char(
                expected = '[',
                onDifferentCharRead = {
                    mustBreak = true
                },
                onIOException = { ioe ->
                    mustBreak = true
                }
            )
            if (!mustBreak) {
                val tagPairResult = peekable.deserializeGameTagPair(currentPosition)
                val (key, value) = tagPairResult.value
                if (tagPairs.containsKey(key)) {
                    // Then we read through a previously empty movetext
                    mustBreak = true
                } else {
                    currentPosition += tagPairResult.charactersRead + 1 // <-- the opening bracket for the tag pair
                    tagPairs[key] = value
                }
            }

            if (mustBreak) {
                break
            }
        }
    }

    val charactersRead = currentPosition - position
    incrementByUTF8CharacterCount(charactersRead)
    return PGNDeserializationResult(charactersRead, PGNGameTags(tagPairs))
}

internal fun BufferedSource.deserializeGameTagPair(position: Int): PGNDeserializationResult<Pair<String, String>> {

    var currentPosition = position
    currentPosition += readWhitespace(position)
    val tagName = deserializePGNSymbolToken(position)
    currentPosition += tagName.charactersRead
    currentPosition += readWhitespace(currentPosition)

    // find the start of the value
    readExpectedUTF8Char(
        expected = '"',
        onIOException = { ioe ->
            throw PGNParseException(currentPosition, "Unexpected error while reading tag pair", ioe)
        },
        onDifferentCharRead = { char ->
            throw PGNUnexpectedTagValueDelimiter(currentPosition, char)
        }
    )
    currentPosition++

    val valueResult = deserializePGNStringToken(currentPosition)
    currentPosition += valueResult.charactersRead
    currentPosition += readWhitespace(currentPosition)

    // find the end of the tag pair
    readExpectedUTF8Char(
        expected = ']',
        onIOException = { ioe ->
            throw PGNParseException(currentPosition, "Unexpected error while reading tag pair", ioe)
        },
        onDifferentCharRead = { char ->
            throw PGNUnexpectedTagTerminator(currentPosition, char)
        }
    )

    val charactersRead = currentPosition - position + 1
    return PGNDeserializationResult(charactersRead, tagName.value to valueResult.value)
}