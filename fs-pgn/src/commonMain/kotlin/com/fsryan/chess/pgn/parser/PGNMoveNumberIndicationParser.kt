package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.use
import kotlin.js.JsName

internal interface PGNMoveNumberIndicationParser: PGNParser<Int>

@JsName("createPGNMoveNumberIndicationParser")
internal fun PGNMoveNumberIndicationParser(): PGNMoveNumberIndicationParser {
    return PGNMoveNumberIndicationParserImpl()
}

private class PGNMoveNumberIndicationParserImpl: PGNMoveNumberIndicationParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<Int> {
        var nextPosition = position + bufferedSource.readWhitespace(position)
        val integerResult = bufferedSource.readIntegerToken(position)
        nextPosition += integerResult.charactersRead

        return bufferedSource.peek().use { peekableSource ->
            val positionBeforePeriods = nextPosition
            while (true) {
                if (peekableSource.exhausted()) {
                    break
                }

                var breakOutOf = false
                peekableSource.readExpectedUTF8Char(
                    expected = '.',
                    onDifferentCharRead = { char ->
                        breakOutOf = true
                    },
                    onIOException = { ioe ->
                        throw PGNParseException(nextPosition, "Unexpected error while reading move number", ioe)
                    }
                )
                if (breakOutOf) {
                    break
                }
                nextPosition++
            }

            bufferedSource.incrementByUTF8CharacterCount(nextPosition - positionBeforePeriods)
            PGNFSMResult(nextPosition - position, integerResult.value)
        }
    }
}