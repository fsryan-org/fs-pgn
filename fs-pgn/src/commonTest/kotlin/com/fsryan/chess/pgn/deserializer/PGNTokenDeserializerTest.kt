package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNIllegalSymbolStartingCharacterException
import com.fsryan.chess.pgn.PGNParseException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.EOFException
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class PGNTokenDeserializerTest {
    
    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReached() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                buf.readPGNSymbolToken(0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading symbol", e.message)
                assertTrue(e.cause is EOFException)
            }
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenInvalidFirstCharacter() {
        sequenceOf('_', '+', '#', '=', ':', '-').forEach { invalidChar ->
            Buffer().use { buf ->
                buf.write(invalidChar.toString().encodeUtf8())
                try {
                    buf.readPGNSymbolToken(0)
                    fail("Should have thrown PGNParseException")
                } catch (e: PGNIllegalSymbolStartingCharacterException) {
                    assertEquals(invalidChar, e.char)
                    assertEquals("Symbol must start with a letter or digit", e.message)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyParseAlphaSymbolStaringWithLowerCaseCharacter() {
        Buffer().use { buf ->
            val expected = "aLPHA"
            buf.write("$expected ".encodeUtf8())
            val actual = buf.readPGNSymbolToken(0)
            assertEquals(expected.length, actual.charactersRead)
        }
    }

    @Test
    fun shouldCorrectlyParseAlphaSymbolStaringWithUpperCaseCharacter() {
        Buffer().use { buf ->
            val expected = "AlphaAgain"
            buf.write("$expected ".encodeUtf8())
            val actual = buf.readPGNSymbolToken(0)
            assertEquals(expected.length, actual.charactersRead)
        }
    }

    @Test
    fun shouldCorrectlyParseAlphaSymbolStaringWithNumericCharacter() {
        Buffer().use { buf ->
            val expected = "1AlphaAgain"
            buf.write("$expected ".encodeUtf8())
            val actual = buf.readPGNSymbolToken(0)
            assertEquals(expected.length, actual.charactersRead)
        }
    }

    @Test
    fun shouldCorrectlyParseSymbolContainingAllowedNonAlphanumericCharacter() {
        sequenceOf('_', '+', '#', '=', ':', '-').forEach { validNonAlphaNumericChar ->
            Buffer().use { buf ->
                val expected = "symb${validNonAlphaNumericChar}ol"
                buf.write("$expected ".encodeUtf8())
                val actual = buf.readPGNSymbolToken(0)
                assertEquals(expected.length, actual.charactersRead)
            }
        }
    }
}