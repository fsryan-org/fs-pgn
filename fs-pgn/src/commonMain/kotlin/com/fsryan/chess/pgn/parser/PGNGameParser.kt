package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameTags
import okio.BufferedSource

interface PGNGameParser: PGNParser<PGNGame>

internal fun PGNGameParser(
    tagSectionParser: PGNTagSectionParser = PGNTagSectionParser(),
    moveTextSectionParser: PGNMoveTextSectionParser = PGNMoveTextSectionParser()
): PGNGameParser = PGNGameParserImpl(tagSectionParser, moveTextSectionParser)

internal fun DefaultPGNGameParser(moveIsBlack: Boolean): PGNGameParser {
    return DefaultPGNGameParserObject
}

private class PGNGameParserImpl(
    private val tagSectionParser: PGNTagSectionParser,
    private val moveTextSectionParser: PGNMoveTextSectionParser
): PGNGameParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGame> {
        return parseGame(tagSectionParser, moveTextSectionParser, bufferedSource, position)
    }
}

private object DefaultPGNGameParserObject: PGNGameParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGame> {
        return parseGame(PGNTagSectionParser(), DefaultPGNMoveTextSectionParser, bufferedSource, position)
    }
}

private fun parseGame(
    tagSectionParser: PGNTagSectionParser,
    moveTextSectionParser: PGNMoveTextSectionParser,
    bufferedSource: BufferedSource,
    position: Int
): PGNParserResult<PGNGame> {
    if (bufferedSource.exhausted()) {
        return PGNFSMResult(value = PGNGame(PGNGameTags(emptyMap()), emptyList()), charactersRead = 0)
    }

    var nextPosition = position + bufferedSource.readWhitespace(position)
    val tagResult = tagSectionParser.parse(bufferedSource, position)
    nextPosition += tagResult.charactersRead + bufferedSource.readWhitespace(nextPosition)
    val moveTextResult = moveTextSectionParser.parse(bufferedSource, nextPosition)
    return PGNFSMResult(
        value = PGNGame(tagResult.value, moveTextResult.value),
        charactersRead = nextPosition - position + moveTextResult.charactersRead
    )
}