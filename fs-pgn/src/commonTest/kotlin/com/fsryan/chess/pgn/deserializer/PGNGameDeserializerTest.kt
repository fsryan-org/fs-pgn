package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import com.fsryan.chess.pgn.dayOfMonth
import com.fsryan.chess.pgn.localDate
import com.fsryan.chess.pgn.monthOfYear
import com.fsryan.chess.pgn.readResourceFile
import com.fsryan.chess.pgn.year
import kotlinx.datetime.LocalDate
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.Path.Companion.toPath
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PGNGameDeserializerTest {

    @Test
    fun shouldReturnEmptyGameWhenEmpty() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val result = buf.deserializePGNGame(0)
            assertTrue(result.value.elements.isEmpty())
            assertEquals(0, result.charactersRead)
        }
    }

    @Test
    fun shouldReadSamplePGNWithNoComments() {
        readResourceFile("one_pgn_no_comments.pgn".toPath()).use { buf ->
            val result = buf.deserializePGNGame(0)
            val actualTags = result.value.tags
            assertEquals("F/S Return Match", actualTags.event)
            assertEquals("Belgrade, Serbia JUG", actualTags.site)
            assertEquals(1992, actualTags.year)
            assertEquals(11, actualTags.monthOfYear)
            assertEquals(4, actualTags.dayOfMonth)
            assertEquals("1992.11.04", actualTags.sevenTagRosterValue(PGNSevenTagRosterTag.Date))
            assertEquals(LocalDate(1992, 11, 4), actualTags.localDate)
            assertEquals("29", actualTags.round)
            assertEquals("Fischer, Robert J.", actualTags.white)
            assertEquals("Spassky, Boris V.", actualTags.black)
            assertEquals(PGNGameResultValue.Draw, actualTags.result)

            assertEquals(86, result.value.elements.size)
        }
    }
}