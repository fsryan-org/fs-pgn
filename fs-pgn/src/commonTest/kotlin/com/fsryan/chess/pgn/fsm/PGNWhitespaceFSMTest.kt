package com.fsryan.chess.pgn.fsm

import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNWhitespaceFSMTest {

    @Test
    fun shouldReturnZeroWhenEmptyInput() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(0, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleSpaceCharacterInput() {
        Buffer().use { buf ->
            buf.write(" ".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(1, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleNewLineCharacterInput() {
        Buffer().use { buf ->
            buf.write("\n".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(1, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleTabCharacterInput() {
        Buffer().use { buf ->
            buf.write("\t".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(1, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn3WhenATabSpaceAndNewlineCharacterArePresent() {
        Buffer().use { buf ->
            buf.write("\t \n".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(3, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn2WhenTwoNewlinesPresent() {
        Buffer().use { buf ->
            buf.write("\n\n".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(2, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn2WhenTwoTabsPresent() {
        Buffer().use { buf ->
            buf.write("\t\t".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(2, result.charactersRead)
        }
    }

    @Test
    fun shouldReturn2WhenTwoSpacesPresent() {
        Buffer().use { buf ->
            buf.write("  ".encodeUtf8())
            val fsmUnderTest = PGNWhitespaceFSM(buf)
            val result = fsmUnderTest.process(0)
            assertEquals(2, result.charactersRead)
        }
    }
}