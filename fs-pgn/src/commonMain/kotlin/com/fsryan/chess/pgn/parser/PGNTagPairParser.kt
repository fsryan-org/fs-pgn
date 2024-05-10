package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedTagTerminator
import com.fsryan.chess.pgn.PGNUnexpectedTagValueDelimiter
import okio.BufferedSource

/**
 * <tag-pair> ::= [ <tag-name> <tag-value> ]
 */
interface PGNTagPairParser: PGNParser<Pair<String, String>>

fun PGNTagPairParser(
    tagNameParser: PGNTagNameParser = PGNTagNameParser(),
    tagValueParser: PGNTagValueParser = PGNTagValueParser()
): PGNTagPairParser {
    return PGNTagPairParserImpl(tagNameParser = tagNameParser, tagValueParser = tagValueParser)
}

fun DefaultPGNTagPairParser(): PGNTagPairParser {
    return DefaultPGNTagPairParserObject
}

private object DefaultPGNTagPairParserObject: PGNTagPairParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<Pair<String, String>> {
        return parseTagPair(PGNTagNameParser(), PGNTagValueParser(), bufferedSource, position)
    }
}

private class PGNTagPairParserImpl(
    private val tagNameParser: PGNTagNameParser,
    private val tagValueParser: PGNTagValueParser
): PGNTagPairParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<Pair<String, String>> {
        return parseTagPair(tagNameParser, tagValueParser, bufferedSource, position)
    }
}

private fun parseTagPair(
    tagNameParser: PGNTagNameParser,
    tagValueParser: PGNTagValueParser,
    bufferedSource: BufferedSource,
    position: Int
): PGNParserResult<Pair<String, String>> {

    var currentPosition = position
    currentPosition += bufferedSource.readWhitespace(position)
    val tagName = tagNameParser.parse(bufferedSource, currentPosition)
    currentPosition += tagName.charactersRead
    currentPosition += bufferedSource.readWhitespace(currentPosition)

    // find the start of the value
    bufferedSource.readExpectedUTF8Char(
        expected = '"',
        onIOException = { ioe ->
            throw PGNParseException(currentPosition, "Unexpected error while reading tag pair", ioe)
        },
        onDifferentCharRead = { char ->
            throw PGNUnexpectedTagValueDelimiter(currentPosition, char)
        }
    )
    currentPosition++

    val valueResult = tagValueParser.parse(bufferedSource, currentPosition)
    currentPosition += valueResult.charactersRead
    currentPosition += bufferedSource.readWhitespace(currentPosition)

    // find the end of the tag pair
    bufferedSource.readExpectedUTF8Char(
        expected = ']',
        onIOException = { ioe ->
            throw PGNParseException(currentPosition, "Unexpected error while reading tag pair", ioe)
        },
        onDifferentCharRead = { char ->
            throw PGNUnexpectedTagTerminator(currentPosition, char)
        }
    )

    val charactersRead = currentPosition - position + 1
    return PGNFSMResult(charactersRead, tagName.value to valueResult.value)
}