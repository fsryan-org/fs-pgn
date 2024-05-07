package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnescapbleCharacterException
import okio.BufferedSource
import okio.IOException

/**
 * A string token is a sequence of zero or more printing characters delimited
 * by a pair of quote characters (ASCII decimal value 34, hexadecimal value
 * 0x22). An empty string is represented by two adjacent quotes. (Note: an
 * apostrophe is not a quote.) A quote inside a string is represented by the
 * backslash immediately followed by a quote. A backslash inside a string is
 * represented by two adjacent backslashes. Strings are commonly used as tag
 * pair values (see below). Non-printing characters like newline and tab are
 * not permitted inside of strings. A string token is terminated by its closing
 * quote.
 */
internal interface PGNStringFSM: PGNTokenFSM<String> {
}

internal fun PGNStringFSM(bufferedSource: BufferedSource): PGNStringFSM {
    return PGNStringFSMImpl(bufferedSource)
}

private class PGNStringFSMImpl(private val bufferedSource: BufferedSource): PGNStringFSM {

    override fun process(position: Int): PGNFSMResult<String> {
        var charactersRead = 0
        var readingEscapedCharacter: Boolean = false
        val buf = StringBuilder()
        try {
            while (true) {
                val codePoint = bufferedSource.readUtf8CodePoint()
                when (val char = Char(codePoint)) {
                    '\\' -> {
                        if (readingEscapedCharacter) {
                            buf.append('\\')
                        }
                        readingEscapedCharacter = !readingEscapedCharacter
                    }
                    '"' -> {
                        if (!readingEscapedCharacter) {
                            charactersRead++
                            break   // <-- end of string
                        }
                        if (readingEscapedCharacter) {
                            buf.append('"')
                            readingEscapedCharacter = false
                        }
                    }
                    else -> when (CharCategory.CONTROL.contains(char)) {
                        true -> throw PGNParseException(
                            position = position + charactersRead,
                            message = "Unexpected control character found while reading string"
                        )
                        false -> {
                            if (readingEscapedCharacter) {
                                throw PGNUnescapbleCharacterException(
                                    position = position + charactersRead,
                                    char = char,
                                    message = "Unexpected character found while attempting to read escaped character"
                                )
                            }
                            buf.append(char)
                        }
                    }
                }
                charactersRead++
            }
            return PGNFSMResult(charactersRead, buf.toString())
        } catch (ioe: IOException) {
            throw PGNParseException(position + charactersRead, "Unexpected end of file while reading string", ioe)
        }
    }
}