package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.IOException
import okio.use

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
                charactersRead += peekable.consumeUTF8CharsAndStopAfter(includeLastCharOnPredicateMatch = true) { it == '\n' }.length
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
    includeLastCharOnPredicateMatch: Boolean = false,
    onIOException: (IOException) -> Unit = {},
    predicate: (Char) -> Boolean,
): String {
    val buf = StringBuilder()
    try {
        while (true) {
            val char = readUTF8Char()
            buf.append(char)
            if (predicate(char)) {
                if (!includeLastCharOnPredicateMatch) {
                    buf.deleteAt(buf.lastIndex)
                }
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