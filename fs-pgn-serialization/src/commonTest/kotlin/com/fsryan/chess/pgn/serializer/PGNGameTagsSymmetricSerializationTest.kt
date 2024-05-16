package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.deserializer.deserializeGameTags
import com.fsryan.chess.pgn.test.TestPGNGameTags
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameTagsSymmetricSerializationTest {

    @Test
    fun shouldSerializeAndDeserializeSymmetrically() {
        (0 until 256).forEach {
            val tags = TestPGNGameTags()
            val serialized = StringBuilder().addPGNGameTags(tags).toString()
            val deserialized = Buffer().use { buf ->
                buf.write(serialized.encodeUtf8())
                buf.deserializeGameTags(0)
            }
            assertEquals(tags, deserialized.value)
        }
    }
}