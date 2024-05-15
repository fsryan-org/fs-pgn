package com.fsryan.chess.pgn.parser

import com.fsryan.chess.fen.FEN_STANDARD_STARTING_POSITION
import com.fsryan.chess.fen.ForsythEdwardsNotation
import com.fsryan.chess.pgn.PGNDuplicateTagException
import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNGameTermination
import com.fsryan.chess.pgn.PGNPlayerType
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import com.fsryan.chess.pgn.annotators
import com.fsryan.chess.pgn.blackELOs
import com.fsryan.chess.pgn.blackNetworkAddresses
import com.fsryan.chess.pgn.blackPlayerTypes
import com.fsryan.chess.pgn.blackPlayers
import com.fsryan.chess.pgn.blackTitles
import com.fsryan.chess.pgn.blackUSCFs
import com.fsryan.chess.pgn.dayOfMonth
import com.fsryan.chess.pgn.localDate
import com.fsryan.chess.pgn.monthOfYear
import com.fsryan.chess.pgn.nonStandardStartingPosition
import com.fsryan.chess.pgn.standardStartingPosition
import com.fsryan.chess.pgn.startingFEN
import com.fsryan.chess.pgn.whiteELOs
import com.fsryan.chess.pgn.whiteNetworkAddresses
import com.fsryan.chess.pgn.whitePlayerTypes
import com.fsryan.chess.pgn.whitePlayers
import com.fsryan.chess.pgn.whiteTitles
import com.fsryan.chess.pgn.whiteUSCFs
import com.fsryan.chess.pgn.year
import kotlinx.datetime.LocalDate
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

    @Test
    fun shouldCorrectlyParseAnnotatorTagWithOneAnnotator() {
        val input = "[Annotator \"John Doe\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("John Doe", actual.value.annotator)
            assertEquals(listOf("John Doe"), actual.value.annotators)
        }
    }

    @Test
    fun shouldCorrectlyParseAnnotatorTagWithMultipleAnnotators() {
        val input = "[Annotator \"Jane Doe:John Doe\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Jane Doe:John Doe", actual.value.annotator)
            assertEquals(listOf("Jane Doe", "John Doe"), actual.value.annotators)
        }
    }

    @Test
    fun shouldCorrectlyParseModeTag() {
        val input = "[Mode \"OTB\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("OTB", actual.value.mode)
        }
    }

    @Test
    fun shouldCorrectlyParsePlyCountTag() {
        val input = "[PlyCount \"42\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(42, actual.value.plyCount)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackPlayers() {
        val input = "[Black \"Jane Doe:John Doe\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Jane Doe:John Doe", actual.value.black)
            assertEquals(listOf("Jane Doe", "John Doe"), actual.value.blackPlayers)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhitePlayers() {
        val input = "[White \"Jane Doe:John Doe\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Jane Doe:John Doe", actual.value.white)
            assertEquals(listOf("Jane Doe", "John Doe"), actual.value.whitePlayers)
        }
    }

    @Test
    fun shouldCorrectlyParseBlackTitle() {
        val input = "[BlackTitle \"WIM\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("WIM", actual.value.blackTitle)
            assertEquals(listOf("WIM"), actual.value.blackTitles)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackTitles() {
        val input = "[BlackTitle \"WIM:WGM\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("WIM:WGM", actual.value.blackTitle)
            assertEquals(listOf("WIM", "WGM"), actual.value.blackTitles)
        }
    }

    @Test
    fun shouldCorrectlyParseWhiteTitle() {
        val input = "[WhiteTitle \"GM\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("GM", actual.value.whiteTitle)
            assertEquals(listOf("GM"), actual.value.whiteTitles)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhiteTitles() {
        val input = "[WhiteTitle \"GM:IM\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("GM:IM", actual.value.whiteTitle)
            assertEquals(listOf("GM", "IM"), actual.value.whiteTitles)
        }
    }

    @Test
    fun shouldCorrectlyParseBlackELO() {
        val input = "[BlackElo \"1234\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(1234, actual.value.blackELOAverage)
            assertEquals(listOf(1234), actual.value.blackELOs)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackELOs() {
        val input = "[BlackElo \"1234:2345\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals((1234 + 2345) / 2, actual.value.blackELOAverage)
            assertEquals(listOf(1234, 2345), actual.value.blackELOs)
        }
    }

    @Test
    fun shouldCorrectlyParseWhiteELO() {
        val input = "[WhiteElo \"1234\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(1234, actual.value.whiteELOAverage)
            assertEquals(listOf(1234), actual.value.whiteELOs)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhiteELOs() {
        val input = "[WhiteElo \"1234:2345\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals((1234 + 2345) / 2, actual.value.whiteELOAverage)
            assertEquals(listOf(1234, 2345), actual.value.whiteELOs)
        }
    }

    @Test
    fun shouldCorrectlyParseBlackUSCF() {
        val input = "[BlackUSCF \"1234\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(1234, actual.value.blackUSCFAverage)
            assertEquals(listOf(1234), actual.value.blackUSCFs)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackUSCFs() {
        val input = "[BlackUSCF \"1234:2345\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals((1234 + 2345) / 2, actual.value.blackUSCFAverage)
            assertEquals(listOf(1234, 2345), actual.value.blackUSCFs)
        }
    }

    @Test
    fun shouldCorrectlyParseWhiteUSCF() {
        val input = "[WhiteUSCF \"1234\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(1234, actual.value.whiteUSCFAverage)
            assertEquals(listOf(1234), actual.value.whiteUSCFs)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhiteUSCFs() {
        val input = "[WhiteUSCF \"1234:2345\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals((1234 + 2345) / 2, actual.value.whiteUSCFAverage)
            assertEquals(listOf(1234, 2345), actual.value.whiteUSCFs)
        }
    }

    @Test
    fun shouldCorrectlyParseBlackNetworkAddress() {
        val input = "[BlackNA \"email+black@example.com\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("email+black@example.com", actual.value.blackNetworkAddress)
            assertEquals(listOf("email+black@example.com"), actual.value.blackNetworkAddresses)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackNetworkAddresses() {
        val input = "[BlackNA \"email+black1@example.com:email+black2@example.com\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("email+black1@example.com:email+black2@example.com", actual.value.blackNetworkAddress)
            assertEquals(listOf("email+black1@example.com", "email+black2@example.com"), actual.value.blackNetworkAddresses)
        }
    }

    @Test
    fun shouldCorrectlyParseWhiteNetworkAddress() {
        val input = "[WhiteNA \"email+white@example.com\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("email+white@example.com", actual.value.whiteNetworkAddress)
            assertEquals(listOf("email+white@example.com"), actual.value.whiteNetworkAddresses)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhiteNetworkAddresses() {
        val input = "[WhiteNA \"email+white1@example.com:email+white2@example.com\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("email+white1@example.com:email+white2@example.com", actual.value.whiteNetworkAddress)
            assertEquals(listOf("email+white1@example.com", "email+white2@example.com"), actual.value.whiteNetworkAddresses)
        }
    }

    @Test
    fun shouldCorrectlyParseBlackPlayerType() {
        val input = "[BlackType \"human\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(PGNPlayerType.Human, actual.value.blackPlayerType)
            assertEquals(listOf(PGNPlayerType.Human), actual.value.blackPlayerTypes)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleBlackPlayerTypes() {
        val input = "[BlackType \"human:program\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(null, actual.value.blackPlayerType)
            assertEquals(listOf(PGNPlayerType.Human, PGNPlayerType.Computer), actual.value.blackPlayerTypes)
        }
    }

    @Test
    fun shouldCorrectlyParseWhitePlayerType() {
        val input = "[WhiteType \"program\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(PGNPlayerType.Computer, actual.value.whitePlayerType)
        }
    }

    @Test
    fun shouldCorrectlyParseMultipleWhitePlayerTypes() {
        val input = "[WhiteType \"human:program\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(null, actual.value.whitePlayerType)
            assertEquals(listOf(PGNPlayerType.Human, PGNPlayerType.Computer), actual.value.whitePlayerTypes)
        }
    }

    @Test
    fun shouldCorrectlyParseSetUpNonstandardPosition() {
        val input = "[SetUp \"1\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertTrue(actual.value.nonStandardStartingPosition())
            assertFalse(actual.value.standardStartingPosition())
        }
    }

    @Test
    fun shouldCorrectlyParseSetUpStandardPosition() {
        val input = "[SetUp \"0\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertFalse(actual.value.nonStandardStartingPosition())
            assertTrue(actual.value.standardStartingPosition())
        }
    }

    @Test
    fun shouldCorrectlyReturnStandardStartingFENWhenFENNotPresent() {
        Buffer().use { buf ->
            val input = "".trimIndent()
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(FEN_STANDARD_STARTING_POSITION, actual.value.fen)
            assertEquals(ForsythEdwardsNotation(FEN_STANDARD_STARTING_POSITION), actual.value.startingFEN())
        }
    }

    @Test
    fun shouldCorrectlyReturnFEN() {
        Buffer().use { buf ->
            val input = "[SetUp \"1\"]\n[FEN \"rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 2\"]"
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 2", actual.value.fen)
            assertEquals(ForsythEdwardsNotation("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 0 2"), actual.value.startingFEN())
        }
    }

    @Test
    fun shouldCorrectlyParseEventSponsor() {
        val input = "[EventSponsor \"ACME\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("ACME", actual.value.eventSponsor)
        }
    }

    @Test
    fun shouldCorrectlyParseEventSection() {
        val input = "[Section \"Open\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Open", actual.value.eventSection)
        }
    }

    @Test
    fun shouldCorrectlyParseEventStage() {
        val input = "[Stage \"Preliminary\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Preliminary", actual.value.eventStage)
        }
    }

    @Test
    fun shouldCorrectlyParseBoardNumber() {
        val input = "[Board \"1\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals(1, actual.value.boardNumber)
        }
    }

    @Test
    fun shouldCorrectlyParseOpening() {
        val input = "[Opening \"Sicilian Defense\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Sicilian Defense", actual.value.opening)
        }
    }

    @Test
    fun shouldCorrectlyParseVariation() {
        val input = "[Variation \"Sicilian Defense: Najdorf Variation\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Sicilian Defense: Najdorf Variation", actual.value.variation)
        }
    }

    @Test
    fun shouldCorrectlyParseSubVariation() {
        val input = "[SubVariation \"Sicilian Defense: Najdorf Variation: English Attack\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Sicilian Defense: Najdorf Variation: English Attack", actual.value.subVariation)
        }
    }

    @Test
    fun shouldCorrectlyParseECO() {
        val input = "[ECO \"B90\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("B90", actual.value.eco)
        }
    }

    @Test
    fun shouldCorrectlyParseNIC() {
        val input = "[NIC \"Sicilian Defense: Najdorf Variation: English Attack\"]"
        Buffer().use { buf ->
            buf.write(input.encodeUtf8())
            val actual = parserUnderTest.parse(buf, 0)
            assertEquals(input.length, actual.charactersRead)
            assertEquals("Sicilian Defense: Najdorf Variation: English Attack", actual.value.nic)
        }
    }

    @Test
    fun shouldCorrectlyParseAllGameTerminations() {
        PGNGameTermination.entries.forEach { termination ->
            val input = "[Termination \"${termination.serialValue}\"]"
            Buffer().use { buf ->
                buf.write(input.encodeUtf8())
                val actual = parserUnderTest.parse(buf, 0)
                assertEquals(input.length, actual.charactersRead)
                assertEquals(termination, actual.value.termination)
            }
        }
    }
}