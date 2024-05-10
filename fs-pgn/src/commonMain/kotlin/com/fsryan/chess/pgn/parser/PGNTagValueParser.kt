package com.fsryan.chess.pgn.parser

import okio.BufferedSource
import kotlin.js.JsName

/**
 * <tag-value> ::= <string>
 */
interface PGNTagValueParser: PGNParser<String>

@JsName("createPGNTagValueParser")
fun PGNTagValueParser(): PGNTagValueParser = PGNTagValueParserObject

private object PGNTagValueParserObject: PGNTagValueParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<String> {
        return bufferedSource.readPGNStringToken(position)
    }
}