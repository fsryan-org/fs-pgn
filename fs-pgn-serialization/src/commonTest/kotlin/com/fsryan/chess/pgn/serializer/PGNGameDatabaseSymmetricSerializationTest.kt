package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.deserializer.deserializePGNGameDatabase
import com.fsryan.chess.pgn.test.TestPGNGameDatabase
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseSymmetricSerializationTest {

    @Test
    fun shouldSerializeAndDeserializeSymmetrically() {
        (0 until 256).forEach {
            val db = TestPGNGameDatabase()
            val serialized = StringBuilder().addPGNGameDatabase(db).toString()
            val deserialized = Buffer().use { buf ->
                buf.write(serialized.encodeUtf8())
                buf.deserializePGNGameDatabase(0)
            }
            assertEquals(db, deserialized.value)
        }
    }
}