package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.PGNParseException
import okio.BufferedSource
import kotlin.jvm.JvmInline

interface PGNGameDatabaseParser: PGNParser<PGNGameDatabase>

fun PGNGameDatabaseParser(
    gameParser: PGNGameParser = PGNGameParser()
): PGNGameDatabaseParser = PGNGameDatabaseParserImpl(gameParser)

@JvmInline
private value class PGNGameDatabaseParserImpl(private val gameParser: PGNGameParser): PGNGameDatabaseParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNParserResult<PGNGameDatabase> {
        val games = mutableListOf<PGNGame>()

        var nextPosition = position
        while (true) {
            nextPosition += bufferedSource.readWhitespace(nextPosition)
            if (bufferedSource.exhausted()) {
                return PGNFSMResult(0, PGNGameDatabase(games.toList()))
            }

            try {
                val gameResult = gameParser.parse(bufferedSource, nextPosition)
                games.add(gameResult.value)
                nextPosition += gameResult.charactersRead
            } catch (e: PGNParseException) {
                break
            }
        }

        return PGNFSMResult(nextPosition - position, PGNGameDatabase(games.toList()))
    }
}