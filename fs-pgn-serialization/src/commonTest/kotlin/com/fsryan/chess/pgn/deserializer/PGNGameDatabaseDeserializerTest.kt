package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.serializer.serialize
import com.fsryan.chess.pgn.test.TestPGNGameDatabase
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseDeserializerTest {

    @Test
    fun shouldDeserializePGNGameDatabase() {
        val expected = TestPGNGameDatabase()
        val serialized = expected.serialize()
        Buffer().use { buf ->
            buf.write(serialized.encodeUtf8())
            val actual = buf.deserializePGNGameDatabase(0)
            assertEquals(expected, actual.value)
        }
    }
}