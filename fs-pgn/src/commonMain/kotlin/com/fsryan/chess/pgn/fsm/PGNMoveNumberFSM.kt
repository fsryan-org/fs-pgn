package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource

interface PGNMoveNumberFSM: PGNFSM<Int>

fun PGNMoveNumberFSM(bufferedSource: BufferedSource): PGNMoveNumberFSM {
    return PGNMoveNumberFSMValue(bufferedSource)
}

private class PGNMoveNumberFSMValue(private val bufferedSource: BufferedSource): PGNMoveNumberFSM {
    override fun process(position: Int): PGNFSMResult<Int> {
        var charactersRead = PGNWhitespaceFSM(bufferedSource).process(position).charactersRead
        val integerResult = PGNIntegerFSM(bufferedSource).process(position + charactersRead)
        charactersRead += integerResult.charactersRead

        val peekableSource = bufferedSource.peek()
        var periodCount = 0
        while (true) {
            if (peekableSource.exhausted()) {
                break
            }

            var breakOutOf = false
            peekableSource.readExpectedUTF8Char(
                expected = '.',
                onDifferentCharRead = { char ->
                    breakOutOf = true
                },
                onIOException = { ioe ->
                    throw PGNParseException(position + charactersRead, "Unexpected error while reading move number", ioe)
                }
            )
            if (breakOutOf) {
                break
            }
            periodCount++
        }

        if (periodCount == 0) {
            return PGNFSMResult(charactersRead, integerResult.value)
        }

        bufferedSource.incrementByUTF8CharacterCount(periodCount)
        return PGNFSMResult(charactersRead + periodCount, integerResult.value)
    }
}