package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnescapbleCharacterException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.EOFException
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PGNStringFSMTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReached() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())

            try {
                val fsmUnderTest = PGNStringFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading string", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenControlCharacterFound() {
        for (controlChar in 0..0x1F) {    // <-- these are all the ASCII control characters
            Buffer().use { buf ->
                buf.write(Char(controlChar).toString().encodeUtf8())

                try {
                    val fsmUnderTest = PGNStringFSM(buf)
                    fsmUnderTest.process(0)
                    fail("Should have thrown PGNParseException")
                } catch (e: PGNParseException) {
                    assertEquals("Unexpected control character found while reading string", e.message)
                }
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenAttemptingToEscapeNonEscapableCharacter() {
        Buffer().use { buf ->
            buf.write("\\a".encodeUtf8())

            try {
                val fsmUnderTest = PGNStringFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNUnescapbleCharacterException) {
                assertEquals('a', e.char)
                assertEquals("Unexpected character found while attempting to read escaped character", e.message)
            }
        }
    }

    @Test
    fun shouldReturnEmptyOnEmptyString() {
        Buffer().use { buf ->
            buf.write("\"".encodeUtf8())
            val fsmUnderTest = PGNStringFSM(buf)
            val output = fsmUnderTest.process(0)
            assertEquals(1, output.charactersRead)
            assertEquals("", output.value)
        }
    }

    @Test
    fun shouldReturnFullStringOnSuccessReadingString() {
        val expected = "This is a full string. Isn't that great?"
        Buffer().use { buf ->
            buf.write("$expected\"".encodeUtf8())
            val fsmUnderTest = PGNStringFSM(buf)
            val output = fsmUnderTest.process(0)
            assertEquals(expected.length + 1, output.charactersRead)    // <-- the final " should be read
            assertEquals(expected, output.value)
        }
    }

    @Test
    fun shouldCorrectlyHandleEscapingAStringWithQuotationMarksInIt() {
        val input = "This is a full string with a \\\". Isn't that great?"
        val expected = "This is a full string with a \". Isn't that great?"
        Buffer().use { buf ->
            buf.write("$input\"".encodeUtf8())
            val fsmUnderTest = PGNStringFSM(buf)
            val output = fsmUnderTest.process(0)
            assertEquals(input.length + 1, output.charactersRead)    // <-- the final " should be read
            assertEquals(expected, output.value)
        }
    }

    @Test
    fun shouldCorrectlyHandleEscapingAStringWithBackslashInIt() {
        val input = "This is a full string with a \\\\. Isn't that great?"
        val expected = "This is a full string with a \\. Isn't that great?"
        Buffer().use { buf ->
            buf.write("$input\"".encodeUtf8())
            val fsmUnderTest = PGNStringFSM(buf)
            val output = fsmUnderTest.process(0)
            assertEquals(input.length + 1, output.charactersRead)    // <-- the final " should be read
            assertEquals(expected, output.value)
        }
    }
}