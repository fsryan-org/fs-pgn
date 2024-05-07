@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlinx.datetime.LocalDate
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNGameTags {
    /**
     * The Black tag value is the name of the player or players of the black
     * pieces. The names are given here as they are for the White tag value.
     * @see white
     */
    val black: String

    /**
     * The 1-indexed day of the month, if known. If unknown, then null
     */
    val dayOfMonth: Int?

    /**
     * The Event tag value should be reasonably descriptive. Abbreviations are
     * to be avoided unless absolutely necessary. A consistent event naming
     * should be used to help facilitate database scanning. If the name of the
     * event is unknown, a single question mark should appear as the tag value.
     */
    val event: String

    /**
     * The 1-indexed month of the year, if known. If unknown, then null
     */
    val monthOfYear: Int?

    val result: PGNGameResult

    /**
     * In a match competition, this value is the number of the game played. If
     * the use of a round number is inappropriate, then the field should be a
     * single hyphen character. If the round is unknown, a single question mark
     * should appear as
     * the tag value.
     */
    val round: String

    /**
     * Include city and region names along with a standard name for the
     * country. The use of the IOC (International Olympic Committee) three
     * letter names is suggested for those countries where such codes are
     * available. If the site of the event is unknown, a single question mark
     * should appear as the tag value. A comma may be used to separate a city
     * from a region. No comma is needed to separate a city or region from the
     * IOC country code. A later section of this document gives a list of three
     * letter nation codes along with a few additions for "locations" not
     * covered by the IOC.
     */
    val site: String

    /**
     * The year of the game, if known. If unknown, then null
     */
    val year: Int?

    /**
     * The White tag value is the name of the player or players of the white
     * pieces. The names are given as they would appear in a telephone
     * directory. The family or last name appears first. If a first name or
     * first initial is available, it is separated from the family name by a
     * comma and a space. Finally, one or more middle initials may appear.
     * (Wherever a comma appears, the very next character should be a space.
     * Wherever an initial appears, the very next character should be a
     * period.) If the name is unknown, a single question mark should appear as
     * the tag value.
     *
     * The intent is to allow meaningful ASCII sorting of the tag value that is
     * independent of regional name formation customs. If more than one person
     * is playing the white pieces, the names are listed in alphabetical order
     * and are separated by the colon character between adjacent entries. A
     * player who is also a computer program should have appropriate version
     * information listed after the name of the program.
     *
     * The format used in the FIDE Rating Lists is appropriate for use for
     * player name tags.
     */
    val white: String

    fun sevenTagRosterValue(tag: PGNSevenTagRosterTag): String

    fun valueOf(tagName: String): String?
}

val PGNGameTags.yearString: String
    get() = year?.toString() ?: "????"

val PGNGameTags.monthOfYearString: String
    get() = monthOfYear?.toString()?.padStart(2, '0') ?: "??"

val PGNGameTags.dayOfMonthString: String
    get() = dayOfMonth?.toString()?.padStart(2, '0') ?: "??"

/**
 * If the year, month, and day of the month are known, then this property will
 * return a [LocalDate] instance. Otherwise, it will return null.
 *
 * @see PGNGameTags.year
 * @see PGNGameTags.monthOfYear
 * @see PGNGameTags.dayOfMonth
 */
val PGNGameTags.localDate: LocalDate?
    get() = dayOfMonth?.let { dom ->
        monthOfYear?.let { moy ->
            year?.let { y ->
                LocalDate(y, moy, dom)
            }
        }
    }

fun PGNGameTags(tagMap: Map<String, String>): PGNGameTags = PGNGameTagsData(tagMap = tagMap)

@JsExport
enum class PGNSevenTagRosterTag {
    Black, Date, Event, Result, Round, Site, White
}

private data class PGNGameTagsData(
    private val tagMap: Map<String, String>
): PGNGameTags {
    override val black: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Black)
    override val dayOfMonth: Int?
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)
            .substring(8, 10)
            .toIntOrNull()
    override val event: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Event)
    override val monthOfYear: Int?
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)
            .substring(5, 7)
            .toIntOrNull()
    override val result: PGNGameResult
        get() = PGNGameResult.fromSerialValue(sevenTagRosterValue(PGNSevenTagRosterTag.Result))
    override val round: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Round)
    override val site: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Site)
    override val year: Int?
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)
            .substring(0, 4)
            .toIntOrNull()
    override val white: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.White)

    override fun sevenTagRosterValue(tag: PGNSevenTagRosterTag): String {
        return tagMap[tag.name] ?: "?"
    }

    override fun valueOf(tagName: String): String? {
        if (PGNSevenTagRosterTag.entries.map { it.name }.contains(tagName)) {
            return sevenTagRosterValue(PGNSevenTagRosterTag.valueOf(tagName))
        }
        return tagMap[tagName]
    }
}