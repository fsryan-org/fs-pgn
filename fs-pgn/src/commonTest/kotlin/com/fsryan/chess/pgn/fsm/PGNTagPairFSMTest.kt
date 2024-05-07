package com.fsryan.chess.pgn.fsm

import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedTagTerminator
import com.fsryan.chess.pgn.PGNUnexpectedTagValueDelimiter
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.EOFException
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PGNTagPairFSMTest {

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReachedWhileReadingSymbol() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                val fsmUnderTest = PGNTagPairFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading symbol", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenNoValueDelimiterFound() {
        Buffer().use { buf ->
            buf.write("KEY ".encodeUtf8())
            try {
                val fsmUnderTest = PGNTagPairFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected error while reading tag pair", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenNoTagTerminatorFound() {
        Buffer().use { buf ->
            buf.write("KEY \"Value\"".encodeUtf8())
            try {
                val fsmUnderTest = PGNTagPairFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected error while reading tag pair", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldCorrectlyReadTagPairWhenCorrectlyFormed() {
        Buffer().use { buf ->
            val expectedKey = "KEY"
            val expectedValue = "Value"
            val expected = expectedKey to expectedValue
            val input = "$expectedKey \"$expectedValue\"]"
            buf.write(input.encodeUtf8())
            val fsmUnderTest = PGNTagPairFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(input.length, result.charactersRead)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldCorrectlyInterpretLeadingInnerAndTrailingWhitespace() {
        Buffer().use { buf ->
            val expectedKey = "KEY"
            val expectedValue = "Value"
            val expected = expectedKey to expectedValue
            val input = "\n\t $expectedKey\n\t \"$expectedValue\"\n\t ]"
            buf.write(input.encodeUtf8())
            val fsmUnderTest = PGNTagPairFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(input.length, result.charactersRead)
            assertEquals(expected, result.value)
        }
    }

    @Test
    fun shouldThrowParseErrorWhenTagTerminatorIsWrongCharacter() {
        Buffer().use { buf ->
            val expectedKey = "KEY"
            val expectedValue = "Value"
            val input = "$expectedKey \"$expectedValue\"}"
            buf.write(input.encodeUtf8())
            try {
                val fsmUnderTest = PGNTagPairFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNUnexpectedTagTerminator")
            } catch (e: PGNUnexpectedTagTerminator) {
                assertEquals(input.length - 1, e.position)
                assertEquals('}', e.char)
            }
        }
    }

    @Test
    fun shouldThrowParseErrorWhenValueDelimiterIsWrongCharacter() {
        Buffer().use { buf ->
            val expectedKey = "KEY"
            val expectedValue = "Value"
            val input = "$expectedKey =\"$expectedValue\"]"
            buf.write(input.encodeUtf8())
            try {
                val fsmUnderTest = PGNTagPairFSM(buf)
                fsmUnderTest.process(0)
                fail("Should have thrown PGNUnexpectedTagValueDelimiter")
            } catch (e: PGNUnexpectedTagValueDelimiter) {
                assertEquals(4, e.position)
                assertEquals('=', e.char)
            }
        }
    }
}