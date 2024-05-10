package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNMoveSectionElement
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.use

/**
 * <movetext-section> ::= <element-sequence> <game-termination>
 */
internal interface PGNMoveTextSectionParser: PGNParser<List<PGNMoveSectionElement>>

internal fun PGNMoveTextSectionParser(
    elementParser: (moveIsBlack: Boolean) -> PGNElementParser,
    gameTerminationParser: PGNGameTerminationParser = PGNGameTerminationParser()
): PGNMoveTextSectionParser = PGNMoveTextSectionParserImpl(elementParser, gameTerminationParser)

private class PGNMoveTextSectionParserImpl(
    private val elementParser: (moveIsBlack: Boolean) -> PGNElementParser,
    private val gameTerminationParser: PGNGameTerminationParser
): PGNMoveTextSectionParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<List<PGNMoveSectionElement>> {
        var moveIsBlack = false
        var nextPosition = position
        val elements = mutableListOf<PGNMoveSectionElement>()
        while (true) {
            if (bufferedSource.exhausted()) {
                break
            }

            nextPosition += bufferedSource.readWhitespace(nextPosition)

            var readNextMove = true
            bufferedSource.peek().use { peekable ->
                try {
                    val terminator = gameTerminationParser.parse(peekable, nextPosition)
                    nextPosition += terminator.charactersRead
                    elements.add(terminator.value)
                    bufferedSource.incrementByUTF8CharacterCount(terminator.charactersRead)
                    return PGNFSMResult(value = elements, charactersRead = nextPosition - position)
                } catch (e: PGNParseException) {
                    // do nothing
                    readNextMove = true
                }
            }

            if (!readNextMove) {
                break
            }

            val result = elementParser(moveIsBlack).parse(bufferedSource, nextPosition)
            elements.add(result.value)
            nextPosition += result.charactersRead
            moveIsBlack = !moveIsBlack
        }

        return PGNFSMResult(value = elements, charactersRead = nextPosition - position)
    }
}