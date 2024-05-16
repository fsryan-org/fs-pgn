package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.plies
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PGNRecursiveVariationDeserializerTest {

    @Test
    fun shouldReturnEmptyListWhenEmpty() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val result = buf.deserializeRecursiveVariation(0, firstMoveIsBlack = false)
            assertEquals(0, result.value.plies.size)
            assertEquals(0, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnSinglePlyWhenUnterminatedButEndOfFile() {
        Buffer().use { buf ->
            val input = "16... e4"
            buf.write(input.encodeUtf8())
            val result = buf.deserializeRecursiveVariation(0, firstMoveIsBlack = true)
            assertEquals(1, result.value.plies.size)
            assertTrue(result.value.plies[0].isBlack)
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnSinglePlyWhenThereIsASinglePly() {
        Buffer().use { buf ->
            val input = "16... e4)"
            buf.write(input.encodeUtf8())
            val result = buf.deserializeRecursiveVariation(0, firstMoveIsBlack = true)
            assertEquals(1, result.value.plies.size)
            assertTrue(result.value.plies[0].isBlack)
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnTwoPliesWhenThereAreTwoPlies() {
        Buffer().use { buf ->
            val input = "16... e4 17. Nf3)"
            buf.write(input.encodeUtf8())
            val result = buf.deserializeRecursiveVariation(0, firstMoveIsBlack = true)
            assertEquals(2, result.value.plies.size)
            assertTrue(result.value.plies[0].isBlack)
            assertFalse(result.value.plies[1].isBlack)
            assertEquals(input.length, result.charactersRead)
        }
    }
}