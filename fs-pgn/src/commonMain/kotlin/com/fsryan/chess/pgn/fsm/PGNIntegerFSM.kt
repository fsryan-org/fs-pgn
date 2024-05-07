package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNInvalidIntegerStartingCharException
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import okio.IOException
import okio.use
import kotlin.jvm.JvmInline

internal interface PGNIntegerFSM: PGNTokenFSM<Int>

internal fun PGNIntegerFSM(bufferedSource: BufferedSource): PGNIntegerFSM {
    return PGNIntegerFSMValue(bufferedSource)
}

@JvmInline
private value class PGNIntegerFSMValue(private val bufferedSource: BufferedSource): PGNIntegerFSM {
    override fun process(position: Int): PGNFSMResult<Int> {
        return bufferedSource.peek().use { peekableSource ->
            var charactersRead = 0
            val buf = StringBuilder()
            try {
                while (true) {
                    when (val char = peekableSource.readUTF8Char()) {
                        '1', '2', '3', '4', '5', '6', '7', '8', '9' -> buf.append(char)
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
                bufferedSource.incrementByUTF8CharacterCount(charactersRead)
                PGNFSMResult(charactersRead, buf.toString().toInt())
            } catch (ioe: IOException) {
                throw PGNParseException(position + charactersRead, "Unexpected end of file while reading integer", ioe)
            }
        }
    }
}