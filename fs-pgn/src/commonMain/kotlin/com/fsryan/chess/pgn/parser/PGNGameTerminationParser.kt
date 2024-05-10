package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedGameTerminationCharException
import com.fsryan.chess.pgn.toPGNGameResult
import okio.BufferedSource
import okio.IOException
import kotlin.js.JsName

interface PGNGameTerminationParser: PGNParser<PGNGameResult>

@JsName("createPGNGameTerminationParser")
fun PGNGameTerminationParser(): PGNGameTerminationParser = PGNGameTerminationParserObject

private object PGNGameTerminationParserObject: PGNGameTerminationParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGameResult> {
        if (bufferedSource.exhausted()) {
            throw PGNParseException(position, "Unexpected end of file while reading game termination")
        }
        // TODO: comments
        try {
            when (val firstChar = bufferedSource.readUTF8Char()) {
                '0' -> when (val secondChar = bufferedSource.readUTF8Char()) {
                    '-' -> when (val thirdChar = bufferedSource.readUTF8Char()) {
                        '1' -> return PGNFSMResult(3, PGNGameResultValue.BlackWins.toPGNGameResult())
                        else -> throw PGNUnexpectedGameTerminationCharException(position + 2, thirdChar)
                    }
                    else -> throw PGNUnexpectedGameTerminationCharException(position + 1, secondChar)
                }
                '1' -> when (val secondChar = bufferedSource.readUTF8Char()) {
                    '-' -> when (val thirdChar = bufferedSource.readUTF8Char()) {
                        '0' -> return PGNFSMResult(3, PGNGameResultValue.WhiteWins.toPGNGameResult())
                        else -> throw PGNUnexpectedGameTerminationCharException(position + 2, thirdChar)
                    }
                    '/' -> when (val thirdChar = bufferedSource.readUTF8Char()) {
                        '2' -> when (val fourthChar = bufferedSource.readUTF8Char()) {
                            '-' -> when (val fifthChar = bufferedSource.readUTF8Char()) {
                                '1' -> when (val sixthChar = bufferedSource.readUTF8Char()) {
                                    '/' -> when (val seventhChar = bufferedSource.readUTF8Char()) {
                                        '2' -> return PGNFSMResult(7, PGNGameResultValue.Draw.toPGNGameResult())
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
                '*' -> return PGNFSMResult(1, PGNGameResultValue.InProgressAbandonedOrUnknown.toPGNGameResult())
                else -> throw PGNUnexpectedGameTerminationCharException(position, firstChar)
            }
        } catch (ioe: IOException) {
            throw PGNParseException(position, "Unexpected error while reading game termination", ioe)
        }
    }
}