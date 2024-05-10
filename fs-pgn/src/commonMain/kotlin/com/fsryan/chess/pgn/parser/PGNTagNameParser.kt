package com.fsryan.chess.pgn.parser

import okio.BufferedSource
import kotlin.js.JsName

/**
 * <tag-name> ::= <identifier>
 */
interface PGNTagNameParser: PGNParser<String>

@JsName("createPGNTagNameParser")
fun PGNTagNameParser(): PGNTagNameParser = PGNTagNameParserImpl()

private class PGNTagNameParserImpl: PGNTagNameParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<String> {
        return bufferedSource.readPGNSymbolToken(position)
    }
}