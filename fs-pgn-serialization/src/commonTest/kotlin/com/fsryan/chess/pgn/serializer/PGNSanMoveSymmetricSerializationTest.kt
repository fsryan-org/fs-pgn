package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.deserializer.deserializeSANMove
import com.fsryan.chess.pgn.pgnString
import com.fsryan.chess.pgn.test.TestPGNSANMove
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNSanMoveSymmetricSerializationTest {

    @Test
    fun shouldSerializeAndDeserializeSymmetrically() {
        (0 until 256).forEach {  index ->
            val isBlack = index % 2 == 1
            val sanMove = TestPGNSANMove(isBlack = isBlack, suffixAnnotation = null)
            val serialized = sanMove.pgnString
            val deserialized = Buffer().use { buf ->
                buf.write(serialized.encodeUtf8())
                buf.deserializeSANMove(0, moveIsBlack = isBlack)
            }
            assertEquals(sanMove, deserialized.value, "pgnString: ${sanMove.pgnString}; expected $sanMove, but was: ${deserialized.value}")
        }
    }
}