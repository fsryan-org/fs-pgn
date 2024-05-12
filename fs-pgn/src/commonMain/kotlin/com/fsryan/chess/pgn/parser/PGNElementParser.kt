package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.PGNSANMove
import okio.BufferedSource
import okio.use
import kotlin.jvm.JvmInline

/**
 * <element> ::= <move-number-indication>
 *               <SAN-move>
 *               <numeric-annotation-glyph>
 *
 * For this, we'll parse all of the above into the same object.
 */
internal interface PGNElementParser: PGNParser<PGNGamePly>

internal fun DefaultPGNElementParser(moveIsBlack: Boolean): PGNElementParser {
    return DefaultPGNElementParserValue(moveIsBlack)
}

internal fun PGNElementParser(
    moveNumberIndicationParser: PGNMoveNumberIndicationParser = PGNMoveNumberIndicationParser(),
    sanMoveParser: PGNSANMoveParser,
    numericAnnotationGlyphParser: PGNNumericAnnotationGlyphParser = PGNNumericAnnotationGlyphParser(),
    recursiveVariationParser: (firstMoveIsBlack: Boolean) -> PGNRecursiveVariationParser = ::DefaultPGNRecursiveVariationParser
): PGNElementParser {
    return PGNElementParserImpl(
        moveNumberIndicationParser = moveNumberIndicationParser,
        sanMoveParser = sanMoveParser,
        numericAnnotationGlyphParser = numericAnnotationGlyphParser,
        recursiveVariationParser = recursiveVariationParser(sanMoveParser.moveIsBlack)
    )
}

@JvmInline
private value class DefaultPGNElementParserValue(private val moveIsBlack: Boolean): PGNElementParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGamePly> {
        return parseElement(
            PGNMoveNumberIndicationParser(),
            PGNSANMoveParser(moveIsBlack),
            PGNNumericAnnotationGlyphParser(),
            PGNRecursiveVariationParser(moveIsBlack, elementParser = ::DefaultPGNElementParser),
            bufferedSource,
            position
        )
    }
}

private class PGNElementParserImpl(
    private val moveNumberIndicationParser: PGNMoveNumberIndicationParser,
    private val sanMoveParser: PGNSANMoveParser,
    private val numericAnnotationGlyphParser: PGNNumericAnnotationGlyphParser,
    private val recursiveVariationParser: PGNRecursiveVariationParser
): PGNElementParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGamePly> {
        return parseElement(
            moveNumberIndicationParser,
            sanMoveParser,
            numericAnnotationGlyphParser,
            recursiveVariationParser,
            bufferedSource,
            position
        )
    }
}

private fun parseElement(
    moveNumberIndicationParser: PGNMoveNumberIndicationParser,
    sanMoveParser: PGNSANMoveParser,
    numericAnnotationGlyphParser: PGNNumericAnnotationGlyphParser,
    recursiveVariationParser: PGNRecursiveVariationParser,
    bufferedSource: BufferedSource,
    position: Int
): PGNParserResult<PGNGamePly> {
    if (bufferedSource.exhausted()) {
        throw PGNParseException(position, "Unexpected end of file while reading PGN element")
    }

    var moveNumber: Int? = null
    var sanMove: PGNSANMove? = null
    var nag: PGNNumericAnnotationGlyph? = null
    var recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = null
    val comments = mutableListOf<String>()

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
                nextChar.isRecursiveAnnotationVariationStart -> {
                    bufferedSource.incrememtByUTF8Char()
                    val recursiveVariationResult = recursiveVariationParser.parse(bufferedSource, nextPosition)
                    recursiveAnnotationVariation = recursiveVariationResult.value
                    nextPosition += recursiveVariationResult.charactersRead + 1
                }
                nextChar.isEndOfLineCommentaryStart -> {
                    bufferedSource.incrememtByUTF8Char()
                    val comment = bufferedSource.consumeUTF8CharsAndStopAfter { it.isEndOfLIneCommentaryEnd }
                    comments.add(comment)
                    nextPosition += comment.length + 2  // <-- initial ; plus newline 
                }
                nextChar.isCurlyCommentaryStart -> {
                    bufferedSource.incrememtByUTF8Char()
                    val comment = bufferedSource.consumeUTF8CharsAndStopAfter { it.isCurlyCommentaryEnd }
                    comments.add(comment)
                    nextPosition += comment.length + 2  // <-- initial { plus }
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
                comments = comments.toList(),
                isBlack = sanMoveParser.moveIsBlack,
                numberIndicator = moveNumber,
                numericAnnotationGlyph = nag,
                recursiveAnnotationVariation = recursiveAnnotationVariation,
                sanMove = it,
            ),
            charactersRead = nextPosition - position
        )
    } ?: throw PGNParseException(position, "No SAN move found")
}