package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNGameTermination
import com.fsryan.chess.pgn.PGNPlayerType
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import com.fsryan.chess.pgn.whiteELOs
import com.fsryan.chess.pgn.whiteNetworkAddresses
import com.fsryan.chess.pgn.whitePlayerTypes
import com.fsryan.chess.pgn.whitePlayerTypesArray
import com.fsryan.chess.pgn.whitePlayers
import com.fsryan.chess.pgn.whitePlayersArray
import com.fsryan.chess.pgn.whiteUSCFs
import kotlin.random.Random

//fun TestPGNGameTags(
//    annotator: String? = randomOptionalUUID(),
//    black: String? = randomOptionalUUID(),
//    blackELOAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
//    blackNetworkAddress: String? = randomOptionalUUID(),
//    blackPlayerType: PGNPlayerType? = randomOptional {
//        randomEnumValue { it != PGNPlayerType.Unknown }
//    },
//    blackTitle: String? = randomOptional { setOf("IM", "GM", "WIM", "WGM").random() },
//    blackUSCFAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
//    boardNumber: Int? = randomOptional { Random.nextInt(until = 10) },
//    dayOfMonth: Int? = randomOptional { Random.nextInt(from = 1, until = 32) },
//    eco: String? = randomOptional(produce = ::randomECO),
//    event: String? = randomOptionalUUID(),
//    eventSponsor: String? = randomOptionalUUID(),
//    eventSection: String? = randomOptionalUUID(),
//    eventStage: String? = randomOptional { setOf("Preliminary", "Semifinal", "Final").random() },
//    fen: String? = randomOptional { randomForsythEdwardsNotation().toString() },
//
//val keysArray: Array<String>
//
///**
// * The 1-indexed month of the year, if known. If unknown, then null
// */
//val monthOfYear: Int?
//    get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)
//        .substring(5, 7)
//        .toIntOrNull()
//
///**
// * This uses a string that gives the playing mode of the game. Examples:
// * "OTB" (over the board), "PM" (paper mail), "EM" (electronic mail), "ICS"
// * (Internet Chess Server), and "TC" (general telecommunication).
// */
//val mode: String?
//    get() = valueOf("Mode")
//
///**
// * This uses a string; this is used for an opening designation from the
// * _New in Chess_ database. This tag pair is associated with the use of the
// * EPD opcode "nic".
// */
//val nic: String?
//    get() = valueOf("NIC")
//
///**
// * This uses a string; this is used for the traditional opening name. This
// * will vary by locale. This tag pair is associated with the use of the EPD
// * opcode "v0"
// */
//val opening: String?
//    get() = valueOf("Opening")
//
///**
// * This tag takes a single integer that gives the number of ply (moves) in
// * the game.
// */
//val plyCount: Int?
//    get() = valueOf("PlyCount")?.toIntOrNull()
//
//val result: PGNGameResultValue
//    get() = PGNGameResultValue.fromSerialValue(sevenTagRosterValue(PGNSevenTagRosterTag.Result))
//
///**
// * In a match competition, this value is the number of the game played. If
// * the use of a round number is inappropriate, then the field should be a
// * single hyphen character. If the round is unknown, a single question mark
// * should appear as
// * the tag value.
// */
//val round: String
//    get() = sevenTagRosterValue(PGNSevenTagRosterTag.Round)
//
///**
// * This tag takes an integer that denotes the "set-up" status of the game.
// * A value of "0" indicates that the game has started from the usual
// * initial array. A value of "1" indicates that the game started from a
// * set-up position; this position is given in the "FEN" tag pair. This tag
// * must appear for a game starting with a set-up position. If it appears
// * with a tag value of "1", a FEN tag pair must also appear.
// *
// * If the tag was not present, then 0 is assumed.
// */
//val setUp: Int
//    get() = valueOf("SetUp")?.toIntOrNull() ?: 0
//
///**
// * Include city and region names along with a standard name for the
// * country. The use of the IOC (International Olympic Committee) three
// * letter names is suggested for those countries where such codes are
// * available. If the site of the event is unknown, a single question mark
// * should appear as the tag value. A comma may be used to separate a city
// * from a region. No comma is needed to separate a city or region from the
// * IOC country code. A later section of this document gives a list of three
// * letter nation codes along with a few additions for "locations" not
// * covered by the IOC.
// */
//val site: String
//    get() = sevenTagRosterValue(PGNSevenTagRosterTag.Site)
//
///**
// * This uses a string; this is used to further refine the Variation tag.
// * This will vary by locale. This tag pair is associated with the use of
// * the EPD opcode "v2"
// */
//val subVariation: String?
//    get() = valueOf("SubVariation")
//
///**
// * This uses a list of one or more time control fields. Each field contains
// * a descriptor for each time control period; if more than one descriptor
// * is present then they are separated by the colon character (":"). The
// * descriptors appear in the order in which they are used in the game. The
// * last field appearing is considered to be implicitly repeated for further
// * control periods as needed.
// */
//val timeControlString: String?
//    get() = valueOf("TimeControl")
//
///**
// * This takes a string that describes the reason for the conclusion of the
// * game. While the Result tag gives the result of the game, it does not
// * provide any extra information and so the Termination tag is defined for this purpose.
// */
//val termination: PGNGameTermination?
//    get() = valueOf("Termination")?.let(PGNGameTermination.Companion::fromSerialValue)
//
///**
// * This uses a string; this is used to further refine the Opening tag. This
// * will vary by locale. This tag pair is associated with the use of the EPD
// * opcode "v1"
// */
//val variation: String?
//    get() = valueOf("Variation")
//
///**
// * The White tag value is the name of the player or players of the white
// * pieces. The names are given as they would appear in a telephone
// * directory. The family or last name appears first. If a first name or
// * first initial is available, it is separated from the family name by a
// * comma and a space. Finally, one or more middle initials may appear.
// * (Wherever a comma appears, the very next character should be a space.
// * Wherever an initial appears, the very next character should be a
// * period.) If the name is unknown, a single question mark should appear as
// * the tag value.
// *
// * The intent is to allow meaningful ASCII sorting of the tag value that is
// * independent of regional name formation customs. If more than one person
// * is playing the white pieces, the names are listed in alphabetical order
// * and are separated by the colon character between adjacent entries. A
// * player who is also a computer program should have appropriate version
// * information listed after the name of the program.
// *
// * The format used in the FIDE Rating Lists is appropriate for use for
// * player name tags.
// *
// * @see black
// * @see whitePlayers
// * @see whitePlayersArray
// */
//val white: String
//    get() = sevenTagRosterValue(PGNSevenTagRosterTag.White)
//
///**
// * If present, this tag gives the AVERAGE of the ratings of all RATED white
// * players as an integer. If only unrated players played, this will return
// * `null`. If there is only one player, and that player is rated, this will
// * return that player's rating.
// * @see whiteELOs
// * @see blackELOAverage
// */
//val whiteELOAverage: Int?
//    get() = whiteELOs.filterNotNull().let {
//        if (it.isEmpty()) null else it.average().toInt()
//    }
//
///**
// * These tags use string values; these are the e-mail or network addresses
// * of the players. A value of "-" is used for a player without an
// * electronic address.
// * @see whiteNetworkAddresses
// * @see blackNetworkAddress
// */
//val whiteNetworkAddress: String?
//    get() = valueOf("WhiteNA")
//
///**
// * These tags use string values; these describe the player types. The value
// * "human" should be used for a person while the value "program" should be
// * used for algorithmic (computer) players.
// *
// * This will be null if there are multiple white players.
// * [whitePlayerTypes] or [whitePlayerTypesArray] may be used to get the
// * player types of all white players.
// *
// * @see blackPlayerType
// * @see whitePlayerTypes
// */
//val whitePlayerType: PGNPlayerType?
//    get() = whitePlayerTypes.let { if (it.size == 1) it[0] else null }
//
///**
// * These use string values such as "FM", "IM", and "GM"; these tags are
// * used only for the standard abbreviations for FIDE titles. A value of "-"
// * is used for an untitled player.
// * @see blackTitle
// */
//val whiteTitle: String?
//    get() = valueOf("WhiteTitle")
//
///**
// * These tags use integer values; these are used for USCF (United States
// * Chess Federation) ratings.
// * @see whiteUSCFs
// * @see blackUSCFAverage
// */
//val whiteUSCFAverage: Int?
//    get() = whiteUSCFs.filterNotNull().let {
//        if (it.isEmpty()) null else it.average().toInt()
//    }
//
///**
// * The year of the game, if known. If unknown, then null
// */
//val year: Int?
//    get() = sevenTagRosterValue(PGNSevenTagRosterTag.Date)
//        .substring(0, 4)
//        .toIntOrNull()
//): PGNGameTags
//
//fun randomECO(): String {
//    val xOptions = "ABCDE"
//    val firstField = "${xOptions.random()}${Random.nextInt(until = 100).toString().padStart(2, '0')}"
//    return when (Random.nextBoolean()) {
//        true -> firstField
//        false -> "$firstField/${Random.nextInt(until = 99).toString().padStart(2, '0')}"
//    }
//}