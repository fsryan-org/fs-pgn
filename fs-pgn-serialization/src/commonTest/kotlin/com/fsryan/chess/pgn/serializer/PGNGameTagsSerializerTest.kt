package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNGameTagsSerializerTest {

    @Test
    fun shouldSerializeAtLeastSevenTagRosterWhenNoSpecificTagsAdded() {
        val input = PGNGameTags(emptyMap())
        val expected = listOf(
            "[Event \"?\"]",
            "[Site \"?\"]",
            "[Date \"????.??.??\"]",
            "[Round \"?\"]",
            "[White \"?\"]",
            "[Black \"?\"]",
            "[Result \"*\"]",
            ""
        )

        val buf = StringBuilder().addPGNGameTags(input)
        val actual = buf.toString().split("\n")

        assertEquals(expected, actual)
    }

    @Test
    fun shouldSerializeAllPopulatedValuesOfTheSevenTagRoster() {
        val input = PGNGameTags(
            mapOf(
                PGNSevenTagRosterTag.Event.name to "F/S Return Match",
                PGNSevenTagRosterTag.Site.name to "Belgrade, Serbia JUG",
                PGNSevenTagRosterTag.Date.name to "1992.11.04",
                PGNSevenTagRosterTag.Round.name to "29",
                PGNSevenTagRosterTag.White.name to "Fischer, Robert J.",
                PGNSevenTagRosterTag.Black.name to "Spassky, Boris V.",
                PGNSevenTagRosterTag.Result.name to "1/2-1/2"
            )
        )
        val expected = listOf(
            "[${PGNSevenTagRosterTag.Event.name} \"F/S Return Match\"]",
            "[${PGNSevenTagRosterTag.Site.name} \"Belgrade, Serbia JUG\"]",
            "[${PGNSevenTagRosterTag.Date.name} \"1992.11.04\"]",
            "[${PGNSevenTagRosterTag.Round.name} \"29\"]",
            "[${PGNSevenTagRosterTag.White.name} \"Fischer, Robert J.\"]",
            "[${PGNSevenTagRosterTag.Black.name} \"Spassky, Boris V.\"]",
            "[${PGNSevenTagRosterTag.Result.name} \"1/2-1/2\"]",
            ""
        )

        val buf = StringBuilder().addPGNGameTags(input)
        val actual = buf.toString().split("\n")

        assertEquals(expected, actual)
    }
}