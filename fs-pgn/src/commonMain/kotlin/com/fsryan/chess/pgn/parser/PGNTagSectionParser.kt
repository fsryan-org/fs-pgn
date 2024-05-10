package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNDuplicateTagException
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource

interface PGNTagSectionParser: PGNParser<PGNGameTags>

fun PGNTagSectionParser(tagPairParser: PGNTagPairParser = PGNTagPairParser()): PGNTagSectionParser {
    return PGNTagSectionParserImpl(tagPairParser)
}

private class PGNTagSectionParserImpl(private val tagPairParser: PGNTagPairParser): PGNTagSectionParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<PGNGameTags> {
        val tagPairs = mutableMapOf<String, String>()
        var currentPosition = position

        while (true) {
            currentPosition += bufferedSource.readWhitespace(position)
            if (bufferedSource.exhausted()) {
                break
            }

            // Checks for the beginning of the move text
            val peekable = bufferedSource.peek()
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
            bufferedSource.incrememtByUTF8Char()
            currentPosition++

            val tagPairResult = tagPairParser.parse(bufferedSource, currentPosition)
            val (key, value) = tagPairResult.value
            if (tagPairs.containsKey(key)) {
                throw PGNDuplicateTagException(currentPosition, key)
            }
            currentPosition += tagPairResult.charactersRead
            tagPairs[key] = value
        }

        val charactersRead = currentPosition - position
        return PGNFSMResult(charactersRead, PGNGameTags(tagPairs))
    }
}