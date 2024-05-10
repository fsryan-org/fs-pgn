package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNSANMove
import okio.BufferedSource
import okio.use

/**
 * <element> ::= <move-number-indication>
 *               <SAN-move>
 *               <numeric-annotation-glyph>
 *
 * For this, we'll parse all of the above.
 */
internal interface PGNElementParser: PGNParser<PGNGamePly>

internal fun PGNElementParser(
    moveNumberIndicationParser: PGNMoveNumberIndicationParser = PGNMoveNumberIndicationParser(),
    sanMoveParser: PGNSANMoveParser,
    numericAnnotationGlyphParser: PGNNumericAnnotationGlyphParser = PGNNumericAnnotationGlyphParser()
): PGNElementParser {
    return PGNElementParserImpl(
        moveNumberIndicationParser = moveNumberIndicationParser,
        sanMoveParser = sanMoveParser,
        numericAnnotationGlyphParser = numericAnnotationGlyphParser
    )
}

private class PGNElementParserImpl(
    private val moveNumberIndicationParser: PGNMoveNumberIndicationParser,
    private val sanMoveParser: PGNSANMoveParser,
    private val numericAnnotationGlyphParser: PGNNumericAnnotationGlyphParser
): PGNElementParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<PGNGamePly> {
        if (bufferedSource.exhausted()) {
            throw PGNParseException(position, "Unexpected end of file while reading PGN element")
        }

        var moveNumber: Int? = null
        var sanMove: PGNSANMove? = null
        var nag: PGNNumericAnnotationGlyph? = null

        var nextPosition = position

        while (true) {
            if (bufferedSource.exhausted()) {
                break
            }
            nextPosition += bufferedSource.readWhitespace(nextPosition)
            var mustBreak = false
            bufferedSource.peek().use { peekable ->
                val nextChar = peekable.readUTF8Char()
                when {
                    nextChar.isDigit() -> when (sanMove) {
                        null -> {
                            val moveNumberResult = moveNumberIndicationParser.parse(bufferedSource, nextPosition)
                            nextPosition += moveNumberResult.charactersRead
                            moveNumber = moveNumberResult.value
                        }
                        else -> mustBreak = true
                    }
                    nextChar.canStartMove -> when(sanMove) {
                        null -> {
                            val sanMoveResult = sanMoveParser.parse(bufferedSource, nextPosition)
                            nextPosition += sanMoveResult.charactersRead
                            sanMove = sanMoveResult.value
                        }
                        else -> mustBreak = true
                    }
                    nextChar.isNAGStart -> when (nag) {
                        null -> {
                            bufferedSource.incrememtByUTF8Char()
                            val nagResult = numericAnnotationGlyphParser.parse(bufferedSource, nextPosition)
                            nextPosition += nagResult.charactersRead + 1
                            nag = nagResult.value
                        }
                        else -> mustBreak = true
                    }
                    else -> mustBreak = true
                }
            }

            if (mustBreak) {
                break
            }
        }

        return sanMove?.let {
             PGNFSMResult(
                value = PGNGamePly(
                    commentsArray = emptyArray(),
                    isBlack = sanMoveParser.moveIsBlack,
                    numberIndicator = moveNumber,
                    numericAnnotationGlyph = nag,
                    recursiveAnnotationVariation = null,    // TODO
                    sanMove = it,
                ),
                charactersRead = nextPosition - position
            )
        } ?: throw PGNParseException(position, "No SAN move found")
    }
}