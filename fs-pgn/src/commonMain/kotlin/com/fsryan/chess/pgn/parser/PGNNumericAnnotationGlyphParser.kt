package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import kotlin.js.JsName

internal interface PGNNumericAnnotationGlyphParser: PGNParser<PGNNumericAnnotationGlyph>

@JsName("createPGNNumericAnnotationGlyphParser")
internal fun PGNNumericAnnotationGlyphParser(): PGNNumericAnnotationGlyphParser {
    return PGNNumericAnnotationGlyphParserImpl()
}

private class PGNNumericAnnotationGlyphParserImpl: PGNNumericAnnotationGlyphParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<PGNNumericAnnotationGlyph> {
        if (bufferedSource.exhausted()) {
            throw PGNParseException(position, "Unexpected end of file while reading NAG")
        }
        val integerResult = bufferedSource.readIntegerToken(position)
        return PGNFSMResult(integerResult.charactersRead, PGNNumericAnnotationGlyph.fromId(integerResult.value))
    }
}