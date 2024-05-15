package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.parser.PGNGameDatabaseParser
import com.fsryan.chess.pgn.readResourceFile
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.Path.Companion.toPath
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameDatabaseSerializerTest {

    @Test
    fun shouldDeserializeAndSerializeNakamuraGames() {
        readResourceFile("Nakamura.pgn".toPath()).use { buf ->
            val result = PGNGameDatabaseParser().parse(buf, 0)
            val serialized = StringBuilder().addPGNGameDatabase(result.value).toString()
            Buffer().use { allGamesBuf ->
                allGamesBuf.write(serialized.encodeUtf8())
                val reDeserialized = PGNGameDatabaseParser().parse(allGamesBuf, 0)
                assertEquals(result.value, reDeserialized.value)
            }
        }

    }
}