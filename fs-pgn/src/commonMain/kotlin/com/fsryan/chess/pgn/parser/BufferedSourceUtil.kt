package com.fsryan.chess.pgn.parser

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
internal fun BufferedSource.readPGNStringToken(position: Int): PGNParserResult<String> {
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
        return PGNFSMResult(charactersRead, buf.toString())
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
internal fun BufferedSource.readPGNSymbolToken(position: Int): PGNParserResult<String> = peek().use { peekable ->
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
        PGNFSMResult(charactersRead, buf.toString())
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
internal fun BufferedSource.readIntegerToken(position: Int): PGNParserResult<Int> = peek().use { peekableSource ->
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
        PGNFSMResult(charactersRead, buf.toString().toInt())
    } catch (ioe: IOException) {
        throw PGNParseException(position + charactersRead, "Unexpected end of file while reading integer", ioe)
    }
}

internal fun BufferedSource.readWhitespace(position: Int): Int = peek().use { peekable ->
    var charactersRead = 0
    try {
        var readingAfterNewline = false
        while (true) {
            if (peekable.exhausted()) {
                break
            }
            var char = peekable.readUTF8Char()
            if (readingAfterNewline && char == '%') {
                charactersRead += peekable.consumeUTF8CharsAndStopAfter { it == '\n' }.length
                char = '\n'
            }
            if (!char.isWhitespace()) {
                break
            }
            charactersRead++
            readingAfterNewline = char == '\n'
        }
        incrementByUTF8CharacterCount(charactersRead)
        return charactersRead
    } catch (ioe: Exception) {
        throw PGNParseException(position + charactersRead, "Unexpected error while reading whitespace", ioe)
    }
}

internal fun BufferedSource.readExpectedUTF8Char(
    expected: Char,
    onIOException: (IOException) -> Unit,
    onDifferentCharRead: (Char) -> Unit
) {
    try {
        val char = readUTF8Char()
        if (char != expected) {
            onDifferentCharRead(char)
        }
    } catch (ioe: IOException) {
        onIOException(ioe)
    }
}

internal fun BufferedSource.incrememtByUTF8Char() = incrementByUTF8CharacterCount(1)

internal fun BufferedSource.incrementByUTF8CharacterCount(count: Int) {
    if (count < 1) {
        return
    }
    for (i in 0 until count) {
        readUtf8CodePoint()
    }
}

/**
 * Consumes UTF-8 characters from the source until the predicate returns true
 *
 * @param onIOException the function to call if an IOException is thrown
 * @param predicate the function that determines when to stop reading
 *
 * @return a String containing the characters read
 */
internal fun BufferedSource.consumeUTF8CharsAndStopAfter(
    onIOException: (IOException) -> Unit = {},
    predicate: (Char) -> Boolean,
): String {
    val buf = StringBuilder()
    try {
        while (true) {
            val char = readUTF8Char()
            buf.append(char)
            if (predicate(char)) {
                break
            }
        }
    } catch (ioe: IOException) {
        onIOException(ioe)
    }
    return buf.toString()
}

internal fun BufferedSource.readUTF8Char(): Char {
    val code = readUtf8CodePoint()
    return Char(code)
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