package com.fsryan.chess.pgn.parser

import okio.BufferedSource
import kotlin.js.JsName

/**
 * <tag-value> ::= <string>
 */
interface PGNTagValueParser: PGNParser<String>

@JsName("createPGNTagValueParser")
fun PGNTagValueParser(): PGNTagValueParser = PGNTagValueParserImpl()

private class PGNTagValueParserImpl: PGNTagValueParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<String> {
        return bufferedSource.readPGNStringToken(position)
    }
}