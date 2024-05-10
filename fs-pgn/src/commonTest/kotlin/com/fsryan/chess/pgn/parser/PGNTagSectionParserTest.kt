package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNDuplicateTagException
import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import com.fsryan.chess.pgn.localDate
import kotlinx.datetime.LocalDate
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNTagSectionParserTest {
    
    val parserUnderTest = PGNTagSectionParser()

    @Test
    fun shouldReturnEmptyMapOnEmptyInput() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val expected = PGNGameTags(emptyMap())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(0, actual.charactersRead)
            assertEquals(expected, actual.value)
        }
    }

    @Test
    fun shouldReturnSingleTagOnSingleTagInput() {
        Buffer().use { buf ->
            val input = "[KEY \"Value\"]"
            val expected = PGNGameTags(mapOf("KEY" to "Value"))
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(expected, actual.value)
        }
    }

    @Test
    fun shouldReturnMultipleTagsOnSingleTagInput() {
        Buffer().use { buf ->
            val tag1 = "[KEY \"Value\"]"
            val tag2 = "[KEY2 \"Value2\"]"
            val input = "$tag1\n$tag2"
            val expected = PGNGameTags(mapOf("KEY" to "Value", "KEY2" to "Value2"))
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(expected, actual.value)
        }
    }

    @Test
    fun shouldThrowParseExceptionWhenDuplicateTagFound() {
        Buffer().use { buf ->
            val tag1 = "[KEY \"Value\"]"
            val tag2 = "[KEY \"Value2\"]"
            val input = "$tag1\n$tag2"
            buf.write(input.encodeUtf8())
            try {
                parserUnderTest.parse(buf, 0)
            } catch (e: PGNDuplicateTagException) {
                assertEquals(tag1.length + 2, e.position)   // <-- the newline and opening tag are added
                assertEquals("KEY", e.key)
            }
        }
    }

    @Test
    fun shouldCorrectlyReadRealisticTagPairs() {
        Buffer().use { buf ->
            val input = """
                [Event "F/S Return Match"]
                [Site "Belgrade, Serbia JUG"]
                [Date "1992.11.04"]
                [Round "29"]
                [White "Fischer, Robert J."]
                [Black "Spassky, Boris V."]
                [Result "1/2-1/2"]
            """.trimIndent()
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("F/S Return Match", actual.value.event)
            assertEquals("Belgrade, Serbia JUG", actual.value.site)
            assertEquals(1992, actual.value.year)
            assertEquals(11, actual.value.monthOfYear)
            assertEquals(4, actual.value.dayOfMonth)
            assertEquals("1992.11.04", actual.value.sevenTagRosterValue(PGNSevenTagRosterTag.Date))
            assertEquals(LocalDate(1992, 11, 4), actual.value.localDate)
            assertEquals("29", actual.value.round)
            assertEquals("Fischer, Robert J.", actual.value.white)
            assertEquals("Spassky, Boris V.", actual.value.black)
            assertEquals(PGNGameResultValue.Draw, actual.value.result)
        }
    }

    @Test
    fun shouldCorrectlyReadRealisticTagPairsWhenMoveTextDelimiterPresent() {
        Buffer().use { buf ->
            val input = """
                [Event "F/S Return Match"]
                [Site "Belgrade, Serbia JUG"]
                [Date "1992.11.04"]
                [Round "29"]
                [White "Fischer, Robert J."]
                [Black "Spassky, Boris V."]
                [Result "1/2-1/2"]
                
                1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3
                O-O 9. h3 Nb8 10. d4 Nbd7 11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15.
                Nb1 h6 16. Bh4 c5 17. dxe5 Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21.
                Nc4 Nxc4 22. Bxc4 Nb6 23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7
                27. Qe3 Qg5 28. Qxg5 hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33.
                f3 Bc8 34. Kf2 Bf5 35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5
                40. Rd6 Kc5 41. Ra6 Nf2 42. g4 Bd3 43. Re6 1/2-1/2
            """.trimIndent()
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(167, actual.charactersRead)
            assertEquals("F/S Return Match", actual.value.event)
            assertEquals("Belgrade, Serbia JUG", actual.value.site)
            assertEquals(1992, actual.value.year)
            assertEquals(11, actual.value.monthOfYear)
            assertEquals(4, actual.value.dayOfMonth)
            assertEquals("1992.11.04", actual.value.sevenTagRosterValue(PGNSevenTagRosterTag.Date))
            assertEquals(LocalDate(1992, 11, 4), actual.value.localDate)
            assertEquals("29", actual.value.round)
            assertEquals("Fischer, Robert J.", actual.value.white)
            assertEquals("Spassky, Boris V.", actual.value.black)
            assertEquals(PGNGameResultValue.Draw, actual.value.result)
        }
    }
}