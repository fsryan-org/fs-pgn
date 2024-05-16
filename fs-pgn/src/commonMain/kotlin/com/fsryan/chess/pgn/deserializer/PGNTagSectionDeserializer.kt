package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNDuplicateTagException
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedTagTerminator
import com.fsryan.chess.pgn.PGNUnexpectedTagValueDelimiter
import okio.BufferedSource

internal fun BufferedSource.deserializeGameTags(position: Int): PGNDeserializationResult<PGNGameTags> {
    val tagPairs = mutableMapOf<String, String>()
    var currentPosition = position

    while (true) {
        currentPosition += readWhitespace(position)
        if (exhausted()) {
            break
        }

        // Checks for the beginning of the move text
        val peekable = peek()
        var mustBreak = false
        peekable.readExpectedUTF8Char(
            expected = '[',
            onDifferentCharRead = {
                mustBreak = true
            },
            onIOException = { ioe ->
                throw PGNParseException(currentPosition, "Unexpected error while reading tag pair", ioe)
            }
        )

        if (mustBreak) {
            break
        }

        // Read the opening bracket for the tag pair
        incrememtByUTF8Char()
        currentPosition++

        val tagPairResult = deserializeGameTagPair(currentPosition)
        val (key, value) = tagPairResult.value
        if (tagPairs.containsKey(key)) {
            throw PGNDuplicateTagException(currentPosition, key)
        }
        currentPosition += tagPairResult.charactersRead
        tagPairs[key] = value
    }

    val charactersRead = currentPosition - position
    return PGNDeserializationResult(charactersRead, PGNGameTags(tagPairs))
}

internal fun BufferedSource.deserializeGameTagPair(position: Int): PGNDeserializationResult<Pair<String, String>> {

    var currentPosition = position
    currentPosition += readWhitespace(position)
    val tagName = readPGNSymbolToken(position)
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

    val valueResult = readPGNStringToken(currentPosition)
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