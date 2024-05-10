package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.all
import com.fsryan.chess.pgn.readResourceFile
import okio.Path.Companion.toPath
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseParserTest {

    internal val parserUnderTest = PGNGameDatabaseParser(
        gameParser = DefaultPGNGameParser(moveIsBlack = false)
    )

    @Test
    fun shouldReadSamplePGNWithNoComments() {
        readResourceFile("Nakamura.pgn".toPath()).use { buf ->
            val result = parserUnderTest.parse(buf, 0)
            assertEquals(7307, result.value.all.size)
        }
    }
}