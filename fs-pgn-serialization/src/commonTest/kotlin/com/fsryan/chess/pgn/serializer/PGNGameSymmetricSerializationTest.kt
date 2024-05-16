package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.deserializer.deserializePGNGame
import com.fsryan.chess.pgn.test.TestPGNGame
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameSymmetricSerializationTest {

    @Test
    fun shouldSerializeAndDeserializeSymmetrically() {
        (0 until 256).forEach {
            val game = TestPGNGame()
            val serialized = StringBuilder().addPGNGame(game).toString()
            val deserialized = Buffer().use { buf ->
                buf.write(serialized.encodeUtf8())
                buf.deserializePGNGame(0)
            }
            assertEquals(game, deserialized.value)
        }
    }
}