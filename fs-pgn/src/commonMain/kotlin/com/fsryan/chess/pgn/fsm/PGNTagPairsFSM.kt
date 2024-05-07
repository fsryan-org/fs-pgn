package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNDuplicateTagException
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedMoveTextDelimiter
import okio.BufferedSource
import kotlin.jvm.JvmInline

interface PGNTagPairsFSM: PGNFSM<PGNGameTags>

fun PGNTagPairsFSM(bufferedSource: BufferedSource): PGNTagPairsFSM {
    return PGNTagPairsFSMValue(bufferedSource)
}

@JvmInline
private value class PGNTagPairsFSMValue(private val bufferedSource: BufferedSource): PGNTagPairsFSM {

    override fun process(position: Int): PGNFSMResult<PGNGameTags> {
        val tagPairs = mutableMapOf<String, String>()
        var charactersRead = 0

        while (true) {
            charactersRead += PGNWhitespaceFSM(bufferedSource).process(position).charactersRead
            if (bufferedSource.exhausted()) {
                break
            }

            // Checks for the beginning of the move text
            val peekable = bufferedSource.peek()
            var mustBreak = false
            peekable.readExpectedUTF8Char(
                expected = '[',
                onDifferentCharRead = { char ->
                    if (char != '1') {
                        throw PGNUnexpectedMoveTextDelimiter(position + charactersRead, char)
                    }
                    mustBreak = true
                },
                onIOException = { ioe ->
                    throw PGNParseException(position + charactersRead, "Unexpected error while reading tag pair", ioe)
                }
            )

            if (mustBreak) {
                break
            }

            // Read the opening bracket for the tag pair
            bufferedSource.incrememtByUTF8Char()
            charactersRead++

            val tagPairResult = PGNTagPairFSM(bufferedSource).process(position + charactersRead)
            val (key, value) = tagPairResult.value
            if (tagPairs.containsKey(key)) {
                throw PGNDuplicateTagException(position + charactersRead, key)
            }
            charactersRead += tagPairResult.charactersRead
            tagPairs[key] = value
        }

        return PGNFSMResult(charactersRead, PGNGameTags(tagPairs))
    }
}