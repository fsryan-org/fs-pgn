package com.fsryan.chess.pgn

import okio.BufferedSource
import kotlin.js.JsName


interface PGNDatabaseParser {
    fun parse(buffer: BufferedSource): PGNDatabase
}

@JsName("createPGNDatabaseParser")
fun PGNDatabaseParser(): PGNDatabaseParser = PGNDatabaseParserImpl()


private class PGNDatabaseParserImpl: PGNDatabaseParser {
    override fun parse(buffer: BufferedSource): PGNDatabase {
        TODO()
    }
}