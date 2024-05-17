package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.all
import com.fsryan.chess.pgn.readResourceFile
import okio.Path.Companion.toPath
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseResourceDeserializerTest {

    @Test
    fun shouldReadNakamuraPGNDatabaseWithNoComments() {
        readResourceFile("Nakamura.pgn").use { buf ->
            val result = buf.deserializePGNGameDatabase(0)
            assertEquals(7307, result.value.all.size)
        }
    }
}