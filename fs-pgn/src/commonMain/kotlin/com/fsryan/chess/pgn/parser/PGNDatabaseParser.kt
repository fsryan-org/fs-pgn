package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNDatabase
import okio.BufferedSource

interface PGNDatabaseParser: PGNParser<PGNDatabase>

fun PGNDatabaseParser(gameParser: PGNGameParser): PGNDatabaseParser = PGNDatabaseParserImpl(gameParser)

private class PGNDatabaseParserImpl(private val gameParser: PGNGameParser): PGNDatabaseParser {
    override fun parse(bufferedSource: BufferedSource, position: Int): PGNFSMResult<PGNDatabase> {
        TODO("Not yet implemented")
    }
}