package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import okio.BufferedSource

internal interface PGNRecursiveVariationParser: PGNParser<PGNRecursiveVariationAnnotation>

internal fun PGNRecursiveVariationParser(
    firstMoveIsBlack: Boolean,
    elementParser: (moveIsBlack: Boolean) -> PGNElementParser
): PGNRecursiveVariationParser = PGNRecursiveVariationParserImpl(elementParser)

private class PGNRecursiveVariationParserImpl(
    private val elementParser: (moveIsBlack: Boolean) -> PGNElementParser
): PGNRecursiveVariationParser {
    override fun parse(
        bufferedSource: BufferedSource,
        position: Int
    ): PGNFSMResult<PGNRecursiveVariationAnnotation> {
        TODO("Not yet implemented")
    }
}