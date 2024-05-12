package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import okio.BufferedSource
import okio.use
import kotlin.jvm.JvmInline

internal interface PGNRecursiveVariationParser: PGNParser<PGNRecursiveVariationAnnotation>

internal fun PGNRecursiveVariationParser(
    firstMoveIsBlack: Boolean,
    elementParser: (moveIsBlack: Boolean) -> PGNElementParser
): PGNRecursiveVariationParser = PGNRecursiveVariationParserImpl(firstMoveIsBlack, elementParser)

internal fun DefaultPGNRecursiveVariationParser(firstMoveIsBlack: Boolean): PGNRecursiveVariationParser {
    return PGNRecursiveVariationParser(
        firstMoveIsBlack = firstMoveIsBlack,
        elementParser = { moveIsBlack -> PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = moveIsBlack)) }
    )
}

private class PGNRecursiveVariationParserImpl(
    private val firstMoveIsBlack: Boolean,
    private val elementParser: (moveIsBlack: Boolean) -> PGNElementParser
): PGNRecursiveVariationParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNRecursiveVariationAnnotation> {
        return parseRecursiveVariation(firstMoveIsBlack, elementParser, bufferedSource, position)
    }
}

@JvmInline
private value class DefaultPGNRecursiveVariationParserValue(private val firstMoveIsBlack: Boolean): PGNRecursiveVariationParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNRecursiveVariationAnnotation> {
        return parseRecursiveVariation(
            firstMoveIsBlack = firstMoveIsBlack,
            elementParser = ::DefaultPGNElementParser,
            bufferedSource = bufferedSource,
            position = position
        )
    }
}

private fun parseRecursiveVariation(
    firstMoveIsBlack: Boolean,
    elementParser: (moveIsBlack: Boolean) -> PGNElementParser,
    bufferedSource: BufferedSource,
    position: Int
): PGNParserResult<PGNRecursiveVariationAnnotation> {
    val plies = mutableListOf<PGNGamePly>()
    var currentMoveIsBlack = firstMoveIsBlack
    var currentPosition = position
    while (true) {
        currentPosition += bufferedSource.readWhitespace(currentPosition)
        if (bufferedSource.exhausted()) {
            return PGNFSMResult(currentPosition - position, PGNRecursiveVariationAnnotation(plies.toList()))
        }
        bufferedSource.peek().use { peekable ->
            val nextChar = peekable.readUTF8Char()
            when {
                nextChar.isRecursiveAnnotationVariationEnd -> {
                    bufferedSource.incrememtByUTF8Char()
                    return PGNFSMResult(currentPosition - position + 1, PGNRecursiveVariationAnnotation(plies.toList()))
                }
                else -> elementParser(currentMoveIsBlack).parse(bufferedSource, currentPosition).let { elementResult ->
                    plies.add(elementResult.value)
                    currentPosition += elementResult.charactersRead
                    currentMoveIsBlack = !currentMoveIsBlack
                }
            }
        }
    }
}