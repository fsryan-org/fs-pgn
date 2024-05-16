package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNGameResultValue
import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.PGNGameTermination
import com.fsryan.chess.pgn.PGNPlayerType
import com.fsryan.chess.pgn.PGNSevenTagRosterTag
import kotlin.random.Random

fun TestPGNGameTags(
    annotator: String? = randomOptionalUUID(),
    black: String = randomUUIDString(),
    blackELOAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
    blackNetworkAddress: String? = randomOptional { randomEmail() },
    blackPlayerType: PGNPlayerType? = randomOptional {
        randomEnumValue<PGNPlayerType> { it != PGNPlayerType.Unknown }
    },
    blackTitle: String? = randomOptional { setOf("IM", "GM", "WIM", "WGM").random() },
    blackUSCFAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
    boardNumber: Int? = randomOptional { Random.nextInt(until = 10) },
    dayOfMonth: Int? = randomOptional(nullRate = 0.05F) { Random.nextInt(from = 1, until = 32) },
    eco: String? = randomOptional(produce = ::randomECO),
    event: String = randomUUIDString(),
    eventSponsor: String? = randomOptionalUUID(),
    eventSection: String? = randomOptionalUUID(),
    eventStage: String? = randomOptional { setOf("Preliminary", "Semifinal", "Final").random() },
    fen: String? = randomOptional { TestForsythEdwardsNotation().toString() },
    mode: String? = randomOptionalUUID(),
    monthOfYear: Int? =randomOptional(nullRate = 0.05F) { Random.nextInt(from = 1, until = 13) },
    nic: String? = randomOptionalUUID(),
    opening: String? = randomOptionalUUID(),
    plyCount: Int? = null,
    result: PGNGameResultValue = randomEnumValue(),
    round: String = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").random(),
    setUp: Int = fen?.let { 1 } ?: 0,
    site: String = randomUUIDString(),
    subVariation: String? = randomOptionalUUID(),
    timeControlString: String? = null,  // TODO: add time control string
    termination: PGNGameTermination? = randomOptional { randomEnumValue<PGNGameTermination>() },
    variation: String? = randomOptionalUUID(),
    white: String = randomUUIDString(),
    whiteELOAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
    whiteNetworkAddress: String? = randomOptional { randomEmail() },
    whitePlayerType: PGNPlayerType? = randomOptional {
        randomEnumValue<PGNPlayerType> { it != PGNPlayerType.Unknown }
    },
    whiteTitle: String? = randomOptional { setOf("IM", "GM", "WIM", "WGM").random() },
    whiteUSCFAverage: Int? = randomOptional { Random.nextInt(until = 4000) },
    year: Int? = randomOptional(nullRate = 0.05F) { Random.nextInt(from = 1800, until = 2100) }
): PGNGameTags {
    val fieldMap = mutableMapOf<String, String>()
    //Event, Site, Date, Round, White, Black, Result
    fieldMap[PGNSevenTagRosterTag.Event.name] = event
    fieldMap[PGNSevenTagRosterTag.Site.name] = site
    val yearString = year?.toString() ?: "????"
    val monthOfYearString = monthOfYear?.toString()?.padStart(2, '0') ?: "??"
    val dayOfMonthString = dayOfMonth?.toString()?.padStart(2, '0') ?: "??"
    fieldMap[PGNSevenTagRosterTag.Date.name] = "${yearString}.${monthOfYearString}.${dayOfMonthString}"
    fieldMap[PGNSevenTagRosterTag.Round.name] = round
    fieldMap[PGNSevenTagRosterTag.White.name] = white
    fieldMap[PGNSevenTagRosterTag.Black.name] = black
    fieldMap[PGNSevenTagRosterTag.Result.name] = result.serialValue
    annotator?.let { fieldMap["Annotator"] = it }
    blackELOAverage?.let { fieldMap["BlackElo"] = it.toString() }
    blackNetworkAddress?.let { fieldMap["BlackNA"] = it }
    blackPlayerType?.let { fieldMap["BlackType"] = it.name }
    blackTitle?.let { fieldMap["BlackTitle"] = it }
    blackUSCFAverage?.let { fieldMap["BlackUSCF"] = it.toString() }
    boardNumber?.let { fieldMap["Board"] = it.toString() }
    eco?.let { fieldMap["ECO"] = it }
    eventSponsor?.let { fieldMap["EventSponsor"] = it }
    eventSection?.let { fieldMap["EventSection"] = it }
    eventStage?.let { fieldMap["EventStage"] = it }
    fen?.let { fieldMap["FEN"] = it }
    mode?.let { fieldMap["Mode"] = it }
    nic?.let { fieldMap["NIC"] = it }
    opening?.let { fieldMap["Opening"] = it }
    plyCount?.let { fieldMap["PlyCount"] = it.toString() }
    fieldMap["SetUp"] = setUp.toString()
    subVariation?.let { fieldMap["SubVariation"] = it }
    timeControlString?.let { fieldMap["TimeControl"] = it }
    termination?.let { fieldMap["Termination"] = it.name }
    variation?.let { fieldMap["Variation"] = it }
    whiteELOAverage?.let { fieldMap["WhiteElo"] = it.toString() }
    whiteNetworkAddress?.let { fieldMap["WhiteNA"] = it }
    whitePlayerType?.let { fieldMap["WhiteType"] = it.name }
    whiteTitle?.let { fieldMap["WhiteTitle"] = it }
    whiteUSCFAverage?.let { fieldMap["WhiteUSCF"] = it.toString() }
    return PGNGameTags(fieldMap)
}

fun randomECO(): String {
    val xOptions = "ABCDE"
    val firstField = "${xOptions.random()}${Random.nextInt(until = 100).toString().padStart(2, '0')}"
    return when (Random.nextBoolean()) {
        true -> firstField
        false -> "$firstField/${Random.nextInt(until = 99).toString().padStart(2, '0')}"
    }
}