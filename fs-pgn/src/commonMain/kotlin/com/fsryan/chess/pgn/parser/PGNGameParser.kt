package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGame
import okio.BufferedSource

interface PGNGameParser: PGNParser<PGNGame>

internal fun PGNGameParser(
    tagSectionParser: PGNTagPairParser,
    moveTextSectionParser: PGNMoveTextSectionParser
): PGNGameParser = PGNGameParserImpl(tagSectionParser, moveTextSectionParser)

private class PGNGameParserImpl(
    private val tagSectionParser: PGNTagPairParser,
    private val moveTextSectionParser: PGNMoveTextSectionParser
): PGNGameParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<PGNGame> {
        TODO("Not yet implemented")
    }
}