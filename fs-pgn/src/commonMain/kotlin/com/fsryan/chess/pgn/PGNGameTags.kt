@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import com.fsryan.chess.fen.FEN_STANDARD_STARTING_POSITION
import com.fsryan.chess.fen.ForsythEdwardsNotation
import kotlinx.datetime.LocalDate
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNGameTags {

    /**
     * This tag uses a name or names in the format of the player name tags;
     * this identifies the annotator or annotators of the game.
     */
    val annotator: String?
        get() = valueOf("Annotator")

    /**
     * The Black tag value is the name of the player or players of the black
     * pieces. The names are given here as they are for the White tag value.
     * @see white
     * @see blackPlayers
     * @see blackPlayersArray
     */
    val black: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Black)

    /**
     * If present, this tag gives the AVERAGE of the ratings of all RATED black
     * players as an integer. If only unrated players played, this will return
     * `null`. If there is only one player, and that player is rated, this will
     * return that player's rating.
     * @see blackELOs
     * @see whiteELOAverage
     */
    val blackELOAverage: Int?
        get() = blackELOs.filterNotNull().let {
            if (it.isEmpty()) null else it.average().toInt()
        }

    /**
     * These tags use string values; these are the e-mail or network addresses
     * of the players. A value of "-" is used for a player without an
     * electronic address.
     * @see blackNetworkAddresses
     * @see whiteNetworkAddress
     */
    val blackNetworkAddress: String?
        get() = valueOf("BlackNA")

    /**
     * These tags use string values; these describe the player types. The value
     * "human" should be used for a person while the value "program" should be
     * used for algorithmic (computer) players.
     *
     * This will be null if there are multiple black players.
     * [blackPlayerTypes] or [blackPlayerTypesArray] may be used to get the
     * player types of all black players.
     *
     * @see blackPlayerTypes
     * @see whitePlayerType
     */
    val blackPlayerType: PGNPlayerType?
        get() = blackPlayerTypes.let { if (it.size == 1) it[0] else null }

    /**
     * These use string values such as "FM", "IM", and "GM"; these tags are
     * used only for the standard abbreviations for FIDE titles. A value of "-"
     * is used for an untitled player.
     * @see whiteTitle
     * @see blackTitles
     * @see blackTitlesArray
     */
    val blackTitle: String?
        get() = valueOf("BlackTitle")

    /**
     * These tags use integer values; these are used for USCF (United States
     * Chess Federation) ratings.
     * @see blackUSCFs
     * @see whiteUSCFAverage
     */
    val blackUSCFAverage: Int?
        get() = blackUSCFs.filterNotNull().let {
            if (it.isEmpty()) null else it.average().toInt()
        }

    /**
     * This uses an integer; this identifies the board number in a team event
     * and also in a simultaneous exhibition.
     */
    val boardNumber: Int?
        get() = valueOf("Board")?.toIntOrNull()

    /**
     * The Date tag value gives the starting date for the game. (Note: this is
     * not necessarily the same as the starting date for the event.) The date
     * is given with respect to the local time of the site given in the Event
     * tag. The Date tag value field always uses a standard ten character
     * format: "YYYY.MM.DD". The first four characters are digits that give the
     * year, the next character is a period, the next two characters are digits
     * that give the month, the next character is a period, and the final two
     * characters are digits that give the day of the month. If any of the
     * digit fields are not known, then question marks are used in place of the
     * digits.
     */
    val date: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)

    /**
     * This uses a string of either the form "XDD" or the form "XDD/DD" where
     * the "X" is a letter from "A" to "E" and the "D" positions are digits;
     * this is used for an opening designation from the five volume
     * _Encyclopedia of Chess Openings_. This tag pair is associated with the
     * use of the EPD opcode "eco"
     */
    val eco: String?
        get() = valueOf("ECO")

    /**
     * The Event tag value should be reasonably descriptive. Abbreviations are
     * to be avoided unless absolutely necessary. A consistent event naming
     * should be used to help facilitate database scanning. If the name of the
     * event is unknown, a single question mark should appear as the tag value.
     */
    val event: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Event)

    /**
     * This uses a string value giving the name of the sponsor of the event.
     */
    val eventSponsor: String?
        get() = valueOf("EventSponsor")

    /**
     * This uses a string; this is used for the playing section of a tournament
     * (e.g., "Open" or "Reserve").
     */
    val eventSection: String?
        get() = valueOf("Section")

    /**
     * This uses a string; this is used for the stage of a multistage event
     * (e.g., "Preliminary" or "Semifinal").
     */
    val eventStage: String?
        get() = valueOf("Stage")

    /**
     * This tag uses a string that gives the Forsyth-Edwards Notation for the
     * starting position used in the game. FEN is described in a later section
     * of this document. If a SetUp tag appears with a tag value of "1", the
     * FEN tag pair is also required.
     */
    val fen: String
        get() = valueOf("FEN") ?: FEN_STANDARD_STARTING_POSITION

    /**
     * An array of all the keys in the tag map. At a minimum, all tags in the
     * [PGNSevenTagRosterTag] enumeration will be returned in their expected
     * order. After the [PGNSevenTagRosterTag] tags, all tags should be
     * returned alphabetically. This is useful for iterating over all tags in a
     * consistent order.
     */
    val keysArray: Array<String>

    /**
     * This uses a string that gives the playing mode of the game. Examples:
     * "OTB" (over the board), "PM" (paper mail), "EM" (electronic mail), "ICS"
     * (Internet Chess Server), and "TC" (general telecommunication).
     */
    val mode: String?
        get() = valueOf("Mode")

    /**
     * This uses a string; this is used for an opening designation from the
     * _New in Chess_ database. This tag pair is associated with the use of the
     * EPD opcode "nic".
     */
    val nic: String?
        get() = valueOf("NIC")

    /**
     * This uses a string; this is used for the traditional opening name. This
     * will vary by locale. This tag pair is associated with the use of the EPD
     * opcode "v0"
     */
    val opening: String?
        get() = valueOf("Opening")

    /**
     * This tag takes a single integer that gives the number of ply (moves) in
     * the game.
     */
    val plyCount: Int?
        get() = valueOf("PlyCount")?.toIntOrNull()

    val result: PGNGameResultValue
        get() = PGNGameResultValue.fromSerialValue(sevenTagRosterValue(PGNSevenTagRosterTag.Result))

    /**
     * In a match competition, this value is the number of the game played. If
     * the use of a round number is inappropriate, then the field should be a
     * single hyphen character. If the round is unknown, a single question mark
     * should appear as
     * the tag value.
     */
    val round: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Round)

    /**
     * This tag takes an integer that denotes the "set-up" status of the game.
     * A value of "0" indicates that the game has started from the usual
     * initial array. A value of "1" indicates that the game started from a
     * set-up position; this position is given in the "FEN" tag pair. This tag
     * must appear for a game starting with a set-up position. If it appears
     * with a tag value of "1", a FEN tag pair must also appear.
     *
     * If the tag was not present, then 0 is assumed.
     */
    val setUp: Int
        get() = valueOf("SetUp")?.toIntOrNull() ?: 0

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
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.Site)

    /**
     * This uses a string; this is used to further refine the Variation tag.
     * This will vary by locale. This tag pair is associated with the use of
     * the EPD opcode "v2"
     */
    val subVariation: String?
        get() = valueOf("SubVariation")

    /**
     * This uses a list of one or more time control fields. Each field contains
     * a descriptor for each time control period; if more than one descriptor
     * is present then they are separated by the colon character (":"). The
     * descriptors appear in the order in which they are used in the game. The
     * last field appearing is considered to be implicitly repeated for further
     * control periods as needed.
     */
    val timeControlString: String?
        get() = valueOf("TimeControl")

    /**
     * This takes a string that describes the reason for the conclusion of the
     * game. While the Result tag gives the result of the game, it does not
     * provide any extra information and so the Termination tag is defined for this purpose.
     */
    val termination: PGNGameTermination?
        get() = valueOf("Termination")?.let(PGNGameTermination.Companion::fromSerialValue)

    /**
     * This uses a string; this is used to further refine the Opening tag. This
     * will vary by locale. This tag pair is associated with the use of the EPD
     * opcode "v1"
     */
    val variation: String?
        get() = valueOf("Variation")

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
     *
     * @see black
     * @see whitePlayers
     * @see whitePlayersArray
     */
    val white: String
        get() = sevenTagRosterValue(PGNSevenTagRosterTag.White)

    /**
     * If present, this tag gives the AVERAGE of the ratings of all RATED white
     * players as an integer. If only unrated players played, this will return
     * `null`. If there is only one player, and that player is rated, this will
     * return that player's rating.
     * @see whiteELOs
     * @see blackELOAverage
     */
    val whiteELOAverage: Int?
        get() = whiteELOs.filterNotNull().let {
            if (it.isEmpty()) null else it.average().toInt()
        }

    /**
     * These tags use string values; these are the e-mail or network addresses
     * of the players. A value of "-" is used for a player without an
     * electronic address.
     * @see whiteNetworkAddresses
     * @see blackNetworkAddress
     */
    val whiteNetworkAddress: String?
        get() = valueOf("WhiteNA")

    /**
     * These tags use string values; these describe the player types. The value
     * "human" should be used for a person while the value "program" should be
     * used for algorithmic (computer) players.
     *
     * This will be null if there are multiple white players.
     * [whitePlayerTypes] or [whitePlayerTypesArray] may be used to get the
     * player types of all white players.
     *
     * @see blackPlayerType
     * @see whitePlayerTypes
     */
    val whitePlayerType: PGNPlayerType?
        get() = whitePlayerTypes.let { if (it.size == 1) it[0] else null }

    /**
     * These use string values such as "FM", "IM", and "GM"; these tags are
     * used only for the standard abbreviations for FIDE titles. A value of "-"
     * is used for an untitled player.
     * @see blackTitle
     */
    val whiteTitle: String?
        get() = valueOf("WhiteTitle")

    /**
     * These tags use integer values; these are used for USCF (United States
     * Chess Federation) ratings.
     * @see whiteUSCFs
     * @see blackUSCFAverage
     */
    val whiteUSCFAverage: Int?
        get() = whiteUSCFs.filterNotNull().let {
            if (it.isEmpty()) null else it.average().toInt()
        }

    fun sevenTagRosterValue(tag: PGNSevenTagRosterTag): String

    fun valueOf(tagName: String): String?
}

val PGNGameTags.annotators: List<String>
    get() = separateDelimitedPlayerInfo(annotator)

val PGNGameTags.blackELOs: List<Int?>
    get() = separateDelimitedPlayerInfo(valueOf("BlackElo")).map { it.toIntOrNull() }

val PGNGameTags.blackNetworkAddresses: List<String>
    get() = separateDelimitedPlayerInfo(blackNetworkAddress)

val PGNGameTags.blackPlayers: List<String>
    get() = separateDelimitedPlayerInfo(black)

val PGNGameTags.blackPlayerTypes: List<PGNPlayerType>
    get() = separateDelimitedPlayerInfo(valueOf("BlackType")).map { PGNPlayerType.fromSerialValue(it) }

val PGNGameTags.blackTitles: List<String>
    get() = separateDelimitedPlayerInfo(blackTitle)

val PGNGameTags.blackUSCFs: List<Int?>
    get() = separateDelimitedPlayerInfo(valueOf("BlackUSCF")).map { it.toIntOrNull() }

/**
 * The 1-indexed day of the month, if known. If unknown, then null
 */
val PGNGameTags.dayOfMonth: Int?
    get() = dayOfMonthString.toIntOrNull()
val PGNGameTags.dayOfMonthString: String
    get() = date.substring(8, 10)

val PGNGameTags.keys: List<String>
    get() = when (this) {
        is PGNGameTagsData -> PGNSevenTagRosterTag.entries.map { it.name }.plus(tagMap.keys.toList()).toSet()
        else -> PGNSevenTagRosterTag.entries.map { it.name }.plus(keysArray.toList()).toSet()
    }.sortedWith { t1, t2 ->
        val str1 = PGNSevenTagRosterTag.entries.firstOrNull { it.name == t1 }
        val str2 = PGNSevenTagRosterTag.entries.firstOrNull { it.name == t2 }
        str1?.let {
            str2?.let {
                str1.ordinal - str2.ordinal
            } ?: -1
        } ?: str2?.let { 1 } ?: t1.compareTo(t2)
    }

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

/**
 * The 1-indexed month of the year, if known. If unknown, then null
 */
val PGNGameTags.monthOfYear: Int?
    get() = monthOfYearString.toIntOrNull()
val PGNGameTags.monthOfYearString: String
    get() = date.substring(5, 7)

val PGNGameTags.whiteELOs: List<Int?>
    get() = separateDelimitedPlayerInfo(valueOf("WhiteElo")).map { it.toIntOrNull() }

val PGNGameTags.whiteNetworkAddresses: List<String>
    get() = separateDelimitedPlayerInfo(whiteNetworkAddress)

val PGNGameTags.whitePlayers: List<String>
    get() = separateDelimitedPlayerInfo(white)

val PGNGameTags.whitePlayerTypes: List<PGNPlayerType>
    get() = separateDelimitedPlayerInfo(valueOf("WhiteType")).map { PGNPlayerType.fromSerialValue(it) }

val PGNGameTags.whiteTitles: List<String>
    get() = separateDelimitedPlayerInfo(whiteTitle)

val PGNGameTags.whiteUSCFs: List<Int?>
    get() = separateDelimitedPlayerInfo(valueOf("WhiteUSCF")).map { it.toIntOrNull() }


/**
 * The year of the game, if known. If unknown, then null
 */
val PGNGameTags.year: Int?
    get() = yearString.toIntOrNull()
val PGNGameTags.yearString: String
    get() = date.substring(0, 4)

@JsExport
fun PGNGameTags.annotatorsArray(): Array<String> = annotators.toTypedArray()

@JsExport
fun PGNGameTags.blackELOsArray(): Array<Int?> = blackELOs.toTypedArray()

@JsExport
fun PGNGameTags.blackNetworkAddressesArray(): Array<String> = blackNetworkAddresses.toTypedArray()

@JsExport
fun PGNGameTags.blackPlayersArray(): Array<String> = blackPlayers.toTypedArray()

@JsExport
fun PGNGameTags.blackPlayerTypesArray(): Array<PGNPlayerType> = blackPlayerTypes.toTypedArray()

@JsExport
fun PGNGameTags.blackTitlesArray(): Array<String> = blackTitles.toTypedArray()

@JsExport
fun PGNGameTags.blackUSCFsArray(): Array<Int?> = blackUSCFs.toTypedArray()

@JsExport
fun PGNGameTags.nonStandardStartingPosition(): Boolean = !standardStartingPosition()

@JsExport
fun PGNGameTags.standardStartingPosition(): Boolean = setUp == 0

@JsExport
fun PGNGameTags.startingFEN(): ForsythEdwardsNotation = ForsythEdwardsNotation(fen)

@JsExport
fun PGNGameTags.whiteELOsArray(): Array<Int?> = whiteELOs.toTypedArray()

@JsExport
fun PGNGameTags.whiteNetworkAddressesArray(): Array<String> = whiteNetworkAddresses.toTypedArray()

@JsExport
fun PGNGameTags.whitePlayersArray(): Array<String> = whitePlayers.toTypedArray()

@JsExport
fun PGNGameTags.whitePlayerTypesArray(): Array<PGNPlayerType> = whitePlayerTypes.toTypedArray()

@JsExport
fun PGNGameTags.whiteTitlesArray(): Array<String> = whiteTitles.toTypedArray()

@JsExport
fun PGNGameTags.whiteUSCFsArray(): Array<Int?> = whiteUSCFs.toTypedArray()

fun PGNGameTags(tagMap: Map<String, String>): PGNGameTags = PGNGameTagsData(tagMap = tagMap)

@JsExport
enum class PGNSevenTagRosterTag {
    Event, Site, Date, Round, White, Black, Result
}

private data class PGNGameTagsData(internal val tagMap: Map<String, String>): PGNGameTags {
    override val keysArray: Array<String>
        get() = keys.toTypedArray()

    override fun sevenTagRosterValue(tag: PGNSevenTagRosterTag): String {
        return tagMap[tag.name] ?: when (tag) {
            PGNSevenTagRosterTag.Date -> "????.??.??"
            PGNSevenTagRosterTag.Result -> PGNGameResultValue.InProgressAbandonedOrUnknown.serialValue
            else -> "?" // this is the default value for all other seven tag roster tags other than Date
        }
    }

    override fun valueOf(tagName: String): String? {
        if (PGNSevenTagRosterTag.entries.map { it.name }.contains(tagName)) {
            return sevenTagRosterValue(PGNSevenTagRosterTag.valueOf(tagName))
        }
        return tagMap[tagName]
    }
}

private fun separateDelimitedPlayerInfo(playerNames: String?): List<String> {
    return playerNames?.split(':')?.map { it.trim() }.orEmpty()
}