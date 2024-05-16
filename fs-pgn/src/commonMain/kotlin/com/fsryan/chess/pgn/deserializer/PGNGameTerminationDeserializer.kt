package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedGameTerminationCharException
import com.fsryan.chess.pgn.toPGNGameResult
import okio.BufferedSource
import okio.IOException

internal fun BufferedSource.deserializeGameTermination(position: Int): PGNDeserializationResult<PGNGameResult> {
    if (exhausted()) {
        throw PGNParseException(position, "Unexpected end of file while reading game termination")
    }
    try {
        when (val firstChar = readUTF8Char()) {
            '0' -> when (val secondChar = readUTF8Char()) {
                '-' -> when (val thirdChar = readUTF8Char()) {
                    '1' -> return PGNDeserializationResult(3, PGNGameResultValue.BlackWins.toPGNGameResult())
                    else -> throw PGNUnexpectedGameTerminationCharException(position + 2, thirdChar)
                }
                else -> throw PGNUnexpectedGameTerminationCharException(position + 1, secondChar)
            }
            '1' -> when (val secondChar = readUTF8Char()) {
                '-' -> when (val thirdChar = readUTF8Char()) {
                    '0' -> return PGNDeserializationResult(3, PGNGameResultValue.WhiteWins.toPGNGameResult())
                    else -> throw PGNUnexpectedGameTerminationCharException(position + 2, thirdChar)
                }
                '/' -> when (val thirdChar = readUTF8Char()) {
                    '2' -> when (val fourthChar = readUTF8Char()) {
                        '-' -> when (val fifthChar = readUTF8Char()) {
                            '1' -> when (val sixthChar = readUTF8Char()) {
                                '/' -> when (val seventhChar = readUTF8Char()) {
                                    '2' -> return PGNDeserializationResult(7, PGNGameResultValue.Draw.toPGNGameResult())
                                    else -> throw PGNUnexpectedGameTerminationCharException(position + 6, seventhChar)
                                }
                                else -> throw PGNUnexpectedGameTerminationCharException(position + 5, sixthChar)
                            }
                            else -> throw PGNUnexpectedGameTerminationCharException(position + 4, fifthChar)
                        }
                        else -> throw PGNUnexpectedGameTerminationCharException(position + 3, fourthChar)
                    }
                    else -> throw PGNUnexpectedGameTerminationCharException(position + 2, thirdChar)
                }
                else -> throw PGNUnexpectedGameTerminationCharException(position + 1, secondChar)
            }
            '*' -> return PGNDeserializationResult(1, PGNGameResultValue.InProgressAbandonedOrUnknown.toPGNGameResult())
            else -> throw PGNUnexpectedGameTerminationCharException(position, firstChar)
        }
    } catch (ioe: IOException) {
        throw PGNParseException(position, "Unexpected error while reading game termination", ioe)
    }
}