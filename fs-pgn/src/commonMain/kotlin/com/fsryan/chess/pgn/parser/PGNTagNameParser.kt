package com.fsryan.chess.pgn.parser

import okio.BufferedSource
import kotlin.js.JsName

/**
 * <tag-name> ::= <identifier>
 */
interface PGNTagNameParser: PGNParser<String>

@JsName("createPGNTagNameParser")
fun PGNTagNameParser(): PGNTagNameParser = PGNTagNameParserObject

private object PGNTagNameParserObject: PGNTagNameParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<String> {
        return bufferedSource.readPGNSymbolToken(position)
    }
}