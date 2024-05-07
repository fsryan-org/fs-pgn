package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.use
import kotlin.jvm.JvmInline

interface PGNWhitespaceFSM: PGNFSM<Unit>

fun PGNWhitespaceFSM(bufferedSource: BufferedSource): PGNWhitespaceFSM {
    return PGNWhitespaceFSMImpl(bufferedSource)
}

@JvmInline
private value class PGNWhitespaceFSMImpl(private val bufferedSource: BufferedSource): PGNWhitespaceFSM {

    override fun process(position: Int): PGNFSMResult<Unit> {
        return bufferedSource.peek().use { peekableSource ->
            var charactersRead = 0
            try {
                var readingAfterNewline = false
                while (true) {
                    if (peekableSource.exhausted()) {
                        break
                    }
                    var char = peekableSource.readUTF8Char()
                    if (readingAfterNewline && char == '%') {
                        charactersRead += peekableSource.consumeUTF8CharsAndStopAfter { it == '\n' }
                        char = '\n'
                    }
                    if (!char.isWhitespace()) {
                        break
                    }
                    charactersRead++
                    readingAfterNewline = char == '\n'
                }
                bufferedSource.incrementByUTF8CharacterCount(charactersRead)
                PGNFSMResult(charactersRead, Unit)
            } catch (ioe: Exception) {
                throw PGNParseException(position + charactersRead, "Unexpected error while reading whitespace", ioe)
            }
        }
    }
}