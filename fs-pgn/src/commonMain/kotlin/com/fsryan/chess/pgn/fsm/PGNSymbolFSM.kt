package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNIllegalSymbolStartingCharacterException
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNSymbolTooLongException
import okio.BufferedSource
import okio.IOException
import okio.use
import kotlin.jvm.JvmInline

/**
 * A symbol token starts with a letter or digit character and is immediately
 * followed by a sequence of zero or more symbol continuation characters.
 * These continuation characters are letter characters ("A-Za-z"), digit
 * characters ("0-9"), the underscore ("_"), the plus sign ("+"), the
 * octothorpe sign ("#"), the equal sign ("="), the colon (":"), and the hyphen
 * ("-"). Symbols are used for a variety of purposes. All characters in a
 * symbol are significant. A symbol token is terminated just prior to the first
 * non-symbol character following the symbol character sequence. Currently, a
 * symbol is limited to a maximum of 255 characters in length.
 */
internal interface PGNSymbolFSM: PGNTokenFSM<String>

internal fun PGNSymbolFSM(bufferedSource: BufferedSource): PGNSymbolFSM {
    return PGNSymbolFSMValue(bufferedSource)
}

@JvmInline
private value class PGNSymbolFSMValue(private val bufferedSource: BufferedSource): PGNSymbolFSM {

    override fun process(position: Int): PGNFSMResult<String> {
        return bufferedSource.peek().use { peekableSource ->
            var charactersRead = 0
            val buf = StringBuilder()
            try {
                while (true) {
                    val char = peekableSource.readUTF8Char()
                    when {
                        char.isValidStartingChar() -> when (charactersRead) {
                            255 -> throw PGNSymbolTooLongException(position)
                            else -> buf.append(char)
                        }
                        else -> when (charactersRead) {
                            0 -> throw PGNIllegalSymbolStartingCharacterException(position, char)
                            else -> when {
                                char.isValidContinuationSymbolChar() -> when (charactersRead) {
                                    255 -> throw PGNSymbolTooLongException(position)
                                    else -> buf.append(char)
                                }
                                else -> break
                            }
                        }
                    }
                    charactersRead++
                    if (peekableSource.exhausted()) {
                        break
                    }
                }
                bufferedSource.incrementByUTF8CharacterCount(charactersRead)
                PGNFSMResult(charactersRead, buf.toString())
            } catch (ioe: IOException) {
                throw PGNParseException(position + charactersRead, "Unexpected end of file while reading symbol", ioe)
            }
        }
    }

    private fun Char.isValidStartingChar(): Boolean {
        if (code < 48) {
            return false
        }
        if (code in 58..64) {
            return false
        }
        if (code in 91..96) {
            return false
        }
        if (code > 122) {
            return false
        }
        return true
    }

    private fun Char.isValidContinuationSymbolChar(): Boolean {
        return this == '_' || this == '+' || this == '#' || this == '=' || this == ':' || this == '-'
    }
}