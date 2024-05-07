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
        return bufferedSource.peek().use { peekableBuffer ->
            var charactersRead = 0
            try {
                while (true) {
                    if (peekableBuffer.exhausted()) {
                        break
                    }
                    val char = peekableBuffer.readUTF8Char()
                    if (char.isWhitespace()) {
                        charactersRead++
                    } else {
                        break
                    }
                }
                bufferedSource.readUTF8CharacterCount(charactersRead)
                PGNFSMResult(charactersRead, Unit)
            } catch (ioe: Exception) {
                throw PGNParseException(position + charactersRead, "Unexpected error while reading whitespace", ioe)
            }
        }
    }
}