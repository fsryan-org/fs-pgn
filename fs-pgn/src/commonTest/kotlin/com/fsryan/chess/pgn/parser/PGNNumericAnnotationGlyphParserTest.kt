package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PGNNumericAnnotationGlyphParserTest {

    internal val parserUnderTest = PGNNumericAnnotationGlyphParser()

    @Test
    fun shouldThrowParseExceptionWhenEndOfFileReachedWhileReading() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
                fail("Should have thrown PGNParseException")
            } catch (e: PGNParseException) {
                assertEquals("Unexpected end of file while reading NAG", e.message)
            }
        }
    }

    @Test
    fun shouldCorrectlyParseKnownNAGs() {
        Buffer().use { buf ->
            PGNNumericAnnotationGlyph.entries.filter { it != PGNNumericAnnotationGlyph.Unknown }.forEach { nag ->
                buf.write(nag.id.toString().encodeUtf8())
                val result = parserUnderTest.parse(buf, 0)
                assertEquals(nag, result.value)
                assertEquals(nag.id.toString().length, result.charactersRead)
            }
        }
    }
}