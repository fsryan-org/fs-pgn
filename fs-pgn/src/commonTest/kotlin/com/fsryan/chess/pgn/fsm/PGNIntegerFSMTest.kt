package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNIntegerException
import com.fsryan.chess.pgn.PGNParseException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.EOFException
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PGNIntegerFSMTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReached() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                val fsmUnderTest = PGNIntegerFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading integer", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenFirstCharacterIsNotANumber() {
        Buffer().use { buf ->
            buf.write("a".encodeUtf8())
            try {
                val fsmUnderTest = PGNIntegerFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNIntegerException) {
                assertEquals("Expected non-zero digit character but found 'a'", e.message)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenFirstCharacterIsZero() {
        Buffer().use { buf ->
            buf.write("01234".encodeUtf8())
            try {
                val fsmUnderTest = PGNIntegerFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNIntegerException) {
                assertEquals("Expected non-zero digit character but found '0'", e.message)
            }
        }
    }

    @Test
    fun shouldReturnIntegerWhenNumberIsSingleDigit() {
        Buffer().use { buf ->
            buf.write("1".encodeUtf8())
            val fsmUnderTest = PGNIntegerFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(1, result.charactersRead)
            assertEquals(1, result.value)
        }
    }

    @Test
    fun shouldReturnIntegerWhenNumberIsMultipleDigits() {
        Buffer().use { buf ->
            buf.write("1234567890".encodeUtf8())
            val fsmUnderTest = PGNIntegerFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(10, result.charactersRead)
            assertEquals(1234567890, result.value)
        }
    }

    @Test
    fun shouldBreakWhenNonDigitCharacterRead() {
        Buffer().use { buf ->
            buf.write("12345a67890".encodeUtf8())
            val fsmUnderTest = PGNIntegerFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(5, result.charactersRead)
            assertEquals(12345, result.value)
        }
    }
}