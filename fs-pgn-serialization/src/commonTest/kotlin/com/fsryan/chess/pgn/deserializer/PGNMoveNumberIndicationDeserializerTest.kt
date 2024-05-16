package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNParseException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.EOFException
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PGNMoveNumberIndicationDeserializerTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReached() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                buf.deserializeMoveNumberIndication(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading integer", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsSingleDigit() {
        Buffer().use { buf ->
            buf.write("1.".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(1, result.value)
            assertEquals(2, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsSingleDigitAndNoPeriodDelimiterFollows() {
        Buffer().use { buf ->
            buf.write("2".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(2, result.value)
            assertEquals(1, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsMultipleDigits() {
        Buffer().use { buf ->
            buf.write("22.".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(22, result.value)
            assertEquals(3, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsMultipleDigitsAndNoPeriodDelimiterFollows() {
        Buffer().use { buf ->
            buf.write("22".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(22, result.value)
            assertEquals(2, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsMultipleDigitsAndMultiplePeriodDelimiersFollow() {
        Buffer().use { buf ->
            buf.write("22...".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(22, result.value)
            assertEquals(5, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnNumberWhenNumberIsMultipleDigitsAndNonDelimiterFollows() {
        Buffer().use { buf ->
            buf.write("22a".encodeUtf8())
            val result = buf.deserializeMoveNumberIndication(0)
            assertEquals(22, result.value)
            assertEquals(2, result.charactersRead)
        }
    }
}