package com.fsryan.chess.pgn.deserializer

import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PGNGameDeserializerTest {

    @Test
    fun shouldReturnEmptyGameWhenEmpty() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val result = buf.deserializePGNGame(0)
            assertTrue(result.value.elementsArray.isEmpty())
            assertEquals(0, result.charactersRead)
        }
    }
}