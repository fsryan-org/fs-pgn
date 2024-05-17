package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.deserializer.deserializePGNGameDatabase
import com.fsryan.chess.pgn.readResourceFile
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.Path.Companion.toPath
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseResourceSerializerTest {

    @Test
    fun shouldDeserializeAndSerializeNakamuraGames() {
        readResourceFile("Nakamura.pgn").use { buf ->
            val result = buf.deserializePGNGameDatabase(0)
            val serialized = StringBuilder().addPGNGameDatabase(result.value).toString()
            Buffer().use { writeBuf ->
                writeBuf.write(serialized.encodeUtf8())
                val reDeserialized = writeBuf.deserializePGNGameDatabase(0)
                assertEquals(result.value, reDeserialized.value)
            }
        }
    }
}