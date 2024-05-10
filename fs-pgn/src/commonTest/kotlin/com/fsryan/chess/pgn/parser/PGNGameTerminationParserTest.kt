package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNUnexpectedGameTerminationCharException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PGNGameTerminationParserTest {

    val parserUnderTest = PGNGameTerminationParser()

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReachedWhileReading() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading game termination", e.message)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharExceptionWhenFirstCharIsNotZeroOneOrAsterisk() {
        Buffer().use { buf ->
            buf.write("X".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('X', e.char)
                assertEquals(0, e.position)
            }
        }
    }

    @Test
    fun shouldParseInProgressAbandonedOrUnknown() {
        Buffer().use { buf ->
            buf.write("*".encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(PGNGameResultValue.InProgressAbandonedOrUnknown, actual.value.result)
            assertEquals(1, actual.charactersRead)
        }
    }

    @Test
    fun shouldParseWhiteWins() {
        Buffer().use { buf ->
            buf.write("1-0".encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(PGNGameResultValue.WhiteWins, actual.value.result)
            assertEquals(3, actual.charactersRead)
        }
    }

    @Test
    fun shouldParseBlackWins() {
        Buffer().use { buf ->
            buf.write("0-1".encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(PGNGameResultValue.BlackWins, actual.value.result)
            assertEquals(3, actual.charactersRead)
        }
    }

    @Test
    fun shouldParseDraw() {
        Buffer().use { buf ->
            buf.write("1/2-1/2".encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(PGNGameResultValue.Draw, actual.value.result)
            assertEquals(7, actual.charactersRead)
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterOnBlackWinsPathWhereSecondCharIsNotHyphen() {
        Buffer().use { buf ->
            buf.write("0X1".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('X', e.char)
                assertEquals(1, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterOnBlackWinsPathWhereThirdCharIsNotOne() {
        Buffer().use { buf ->
            buf.write("0-2".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('2', e.char)
                assertEquals(2, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterOnWhiteWinOrDrawPathWhereSecondCharacterIsNotHyphenOrSlash() {
        Buffer().use { buf ->
            buf.write("1X0".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('X', e.char)
                assertEquals(1, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnWhiteWinPathWhenThirdCharacterNotZero() {
        Buffer().use { buf ->
            buf.write("1-1".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('1', e.char)
                assertEquals(2, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnDrawPathWhenThirdCharacterNot2() {
        Buffer().use { buf ->
            buf.write("1/3-1/2".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('3', e.char)
                assertEquals(2, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnDrawPathWhenFourthCharacterNotHyphen() {
        Buffer().use { buf ->
            buf.write("1/2X1/2".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('X', e.char)
                assertEquals(3, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnDrawPathWhenFifthCharacterNotOne() {
        Buffer().use { buf ->
            buf.write("1/2-2/2".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('2', e.char)
                assertEquals(4, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnDrawPathWhenSixthCharacterNotSlash() {
        Buffer().use { buf ->
            buf.write("1/2-1X2".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('X', e.char)
                assertEquals(5, e.position)
            }
        }
    }

    @Test
    fun shouldThrowPGNUnexpectedGameTerminationCharacterExceptionOnDrawPathWhenSeventhCharacterNotTwo() {
        Buffer().use { buf ->
            buf.write("1/2-1/3".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNUnexpectedGameTerminationCharException")
            } catch (e: PGNUnexpectedGameTerminationCharException) {
                assertEquals("Unexpected character found while reading game termination", e.message)
                assertEquals('3', e.char)
                assertEquals(6, e.position)
            }
        }
    }
}