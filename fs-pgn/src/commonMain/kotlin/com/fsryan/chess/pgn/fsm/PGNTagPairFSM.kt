package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedTagTerminator
import com.fsryan.chess.pgn.PGNUnexpectedTagValueDelimiter
import okio.BufferedSource

internal interface PGNTagPairFSM: PGNFSM<Pair<String, String>>

internal fun PGNTagPairFSM(bufferedSource: BufferedSource): PGNTagPairFSM {
    return PGNTagPairFSMImpl(bufferedSource)
}

private class PGNTagPairFSMImpl(private val bufferedSource: BufferedSource): PGNTagPairFSM {

    override fun process(position: Int): PGNFSMResult<Pair<String, String>> {
        var charactersRead = PGNWhitespaceFSM(bufferedSource).process(position).charactersRead

        val keyResult = PGNSymbolFSM(bufferedSource).process(position + charactersRead)
        charactersRead += keyResult.charactersRead

        // read the whitespace after the key
        charactersRead += PGNWhitespaceFSM(bufferedSource).process(position + charactersRead).charactersRead

        // find the start of the value
        bufferedSource.readExpectedUTF8Char(
            expected = '"',
            onIOException = { ioe ->
                throw PGNParseException(position + charactersRead, "Unexpected error while reading tag pair", ioe)
            },
            onDifferentCharRead = { char ->
                throw PGNUnexpectedTagValueDelimiter(position + charactersRead, char)
            }
        )
        charactersRead++

        // read the value
        val valueResult = PGNStringFSM(bufferedSource).process(position + charactersRead)
        charactersRead += valueResult.charactersRead

        // Read any trailing whitespace before the terminal character
        charactersRead += PGNWhitespaceFSM(bufferedSource).process(position + charactersRead).charactersRead

        bufferedSource.readExpectedUTF8Char(
            expected = ']',
            onIOException = { ioe ->
                throw PGNParseException(position + charactersRead, "Unexpected error while reading tag pair", ioe)
            },
            onDifferentCharRead = { char ->
                throw PGNUnexpectedTagTerminator(position + charactersRead, char)
            }
        )
        return PGNFSMResult(charactersRead + 1, keyResult.value to valueResult.value)
    }
}