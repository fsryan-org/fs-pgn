package com.fsryan.chess.pgn.fsm

import okio.BufferedSource
import okio.IOException

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

internal fun BufferedSource.readUTF8CharacterCount(count: Int) {
    for (i in 0 until count) {
        readUtf8CodePoint()
    }
}

internal fun BufferedSource.readUTF8Char(): Char {
    val code = readUtf8CodePoint()
    return Char(code)
}