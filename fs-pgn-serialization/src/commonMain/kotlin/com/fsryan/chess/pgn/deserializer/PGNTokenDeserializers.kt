package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNCannotEscapeCharacterException
import com.fsryan.chess.pgn.PGNIllegalSymbolStartingCharacterException
import com.fsryan.chess.pgn.PGNInvalidIntegerStartingCharException
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNStringControlCharacterFoundException
import com.fsryan.chess.pgn.PGNStringTooLongException
import com.fsryan.chess.pgn.PGNSymbolTooLongException
import okio.BufferedSource
import okio.IOException
import okio.use

/**
 * A string token is a sequence of zero or more printing characters delimited
 * by a pair of quote characters (ASCII decimal value 34, hexadecimal value
 * 0x22). An empty string is represented by two adjacent quotes. (Note: an
 * apostrophe is not a quote.) A quote inside a string is represented by the
 * backslash immediately followed by a quote. A backslash inside a string is
 * represented by two adjacent backslashes. Strings are commonly used as tag
 * pair values (see below). Non-printing characters like newline and tab are
 * not permitted inside of strings. A string token is terminated by its closing
 * quote. Currently, a string is limited to a maximum of 255 characters of
 * data.
 */
internal fun BufferedSource.deserializePGNStringToken(position: Int): PGNDeserializationResult<String> {
    var charactersRead = 0
    var readingEscapedCharacter = false
    val buf = StringBuilder()
    try {
        while (true) {
            when (val char = readUTF8Char()) {
                '\\' -> {
                    if (readingEscapedCharacter) {
                        buf.appendIfWithinLimit(readLimit = 255, charactersRead, '\\') {
                            throw PGNStringTooLongException(position)
                        }
                    }
                    readingEscapedCharacter = !readingEscapedCharacter
                }
                '"' -> {
                    if (!readingEscapedCharacter) {
                        charactersRead++
                        break   // <-- end of string
                    }
                    if (readingEscapedCharacter) {
                        buf.appendIfWithinLimit(readLimit = 255, charactersRead, '"') {
                            throw PGNStringTooLongException(position)
                        }
                        readingEscapedCharacter = false
                    }
                }
                else -> when (CharCategory.CONTROL.contains(char)) {
                    true -> throw PGNStringControlCharacterFoundException(position + charactersRead, char = char)
                    false -> {
                        if (readingEscapedCharacter) {
                            throw PGNCannotEscapeCharacterException(position + charactersRead, char = char)
                        }
                        buf.appendIfWithinLimit(readLimit = 255, charactersRead, char) {
                            throw PGNStringTooLongException(position)
                        }
                    }
                }
            }
            charactersRead++
        }
        return PGNDeserializationResult(charactersRead, buf.toString())
    } catch (ioe: IOException) {
        throw PGNParseException(position + charactersRead, "Unexpected end of file while reading string", ioe)
    }
}

/**
 * A symbol token starts with a letter or digit character and is immediately
 * followed by a sequence of zero or more symbol continuation characters.
 * These continuation characters are letter characters ("A-Za-z"), digit
 * characters ("0-9"), the underscore ("_"), the plus sign ("+"), the
 * octothorpe sign ("#"), the equal sign ("="), the colon (":"), and the hyphen
 * ("-").
 *
 * Symbols are used for a variety of purposes. All characters in a symbol are
 * significant. A symbol token is terminated just prior to the first non-symbol
 * character following the symbol character sequence. Currently, a symbol is
 * limited to a maximum of 255 characters in length.
 */
internal fun BufferedSource.deserializePGNSymbolToken(position: Int): PGNDeserializationResult<String> = peek().use { peekable ->
    var charactersRead = 0
    val buf = StringBuilder()
    try {
        while (true) {
            val char = peekable.readUTF8Char()
            when (charactersRead) {
                0 -> when {
                    char.isValidStartingSymbolChar -> buf.append(char)
                    else -> throw PGNIllegalSymbolStartingCharacterException(position, char)
                }
                else -> when (char.isValidSymbolContinuationChar) {
                    true -> buf.appendIfWithinLimit(readLimit = 255, charactersRead, char) {
                        throw PGNSymbolTooLongException(position)
                    }
                    false -> break
                }
            }
            charactersRead++
            if (peekable.exhausted()) {
                break
            }
        }
        incrementByUTF8CharacterCount(charactersRead)
        PGNDeserializationResult(charactersRead, buf.toString())
    } catch (ioe: IOException) {
        throw PGNParseException(position + charactersRead, "Unexpected end of file while reading symbol", ioe)
    }
}

/**
 * An integer token is a sequence of one or more decimal digit characters. It
 * is a special case of the more general "symbol" token class described below.
 * Integer tokens are used to help represent move number indications. An
 * integer token is terminated just prior to the first non-symbol character
 * following the integer digit sequence.
 */
internal fun BufferedSource.readIntegerToken(position: Int): PGNDeserializationResult<Int> = peek().use { peekableSource ->
    var charactersRead = 0
    val buf = StringBuilder()
    try {
        while (true) {
            when (val char = peekableSource.readUTF8Char()) {
                in '1' .. '9' -> buf.append(char)
                '0' -> when (charactersRead) {
                    0 -> throw PGNInvalidIntegerStartingCharException(position = position, char = char)
                    else -> buf.append(char)
                }
                else -> when (charactersRead) {
                    0 -> throw PGNInvalidIntegerStartingCharException(position = position, char = char)
                    else -> break
                }
            }
            charactersRead++
            if (peekableSource.exhausted()) {
                break
            }
        }
        incrementByUTF8CharacterCount(charactersRead)
        PGNDeserializationResult(charactersRead, buf.toString().toInt())
    } catch (ioe: IOException) {
        throw PGNParseException(position + charactersRead, "Unexpected end of file while reading integer", ioe)
    }
}

private fun StringBuilder.appendIfWithinLimit(
    readLimit: Int,
    readChars: Int,
    char: Char,
    onLimitExceeded: () -> Unit
): StringBuilder {
    if (readChars >= readLimit) {
        onLimitExceeded()
        return this
    }
    return append(char)
}