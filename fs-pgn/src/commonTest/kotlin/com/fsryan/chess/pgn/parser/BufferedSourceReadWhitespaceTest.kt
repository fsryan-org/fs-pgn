package com.fsryan.chess.pgn.parser

import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class BufferedSourceReadWhitespaceTest {

    @Test
    fun shouldReturnZeroWhenEmptyInput() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(0, actual)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleSpaceCharacterInput() {
        Buffer().use { buf ->
            buf.write(" ".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(1, actual)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleNewLineCharacterInput() {
        Buffer().use { buf ->
            buf.write("\n".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(1, actual)
        }
    }

    @Test
    fun shouldReturn1WhenJustASingleTabCharacterInput() {
        Buffer().use { buf ->
            buf.write("\t".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(1, actual)
        }
    }

    @Test
    fun shouldReturn3WhenATabSpaceAndNewlineCharacterArePresent() {
        Buffer().use { buf ->
            buf.write("\t \n".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(3, actual)
        }
    }

    @Test
    fun shouldReturn2WhenTwoNewlinesPresent() {
        Buffer().use { buf ->
            buf.write("\n\n".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(2, actual)
        }
    }

    @Test
    fun shouldReturn2WhenTwoTabsPresent() {
        Buffer().use { buf ->
            buf.write("\t\t".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(2, actual)
        }
    }

    @Test
    fun shouldReturn2WhenTwoSpacesPresent() {
        Buffer().use { buf ->
            buf.write("  ".encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(2, actual)
        }
    }

    @Test
    fun shouldProperlyCountWhitespaceAndLineEscapeMechanism() {
        Buffer().use { buf ->
            val input = "\n% This is a bunch of text that should be ignored"
            buf.write(input.encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(input.length, actual)
        }
    }

    @Test
    fun shouldProperlyCountWhitespaceAndLineEscapeMechanismWhenTheLineEscapeIsTerminatedWithNewline() {
        Buffer().use { buf ->
            val nextLine = "Something else"
            val input = "\n% This is a bunch of text that should be ignored\n$nextLine"
            buf.write(input.encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(input.length - nextLine.length, actual)
        }
    }

    @Test
    fun shouldProperlyCountWhitespaceAndLineEscapeMechanismOnMultipleLines() {
        Buffer().use { buf ->
            val nextLine = "Something else"
            val comment1 = "\n% This is a bunch of text that should be ignored"
            val comment2 = "\n% This is a bunch more text that should be ignored\n"
            val input = "$comment1$comment2\n$nextLine"
            buf.write(input.encodeUtf8())
            val actual = buf.readWhitespace(0)
            assertEquals(input.length - nextLine.length, actual)
        }
    }
}