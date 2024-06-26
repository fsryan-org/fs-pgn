package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNGame
import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.all
import com.fsryan.chess.pgn.elements

fun PGNGameDatabase.serialize(): String = StringBuilder().addPGNGameDatabase(this).toString()

internal fun StringBuilder.addPGNGameDatabase(database: PGNGameDatabase): StringBuilder {
    database.all.forEach { game ->
        addPGNGame(game)
        append("\n")
    }
    return this
}

internal fun StringBuilder.addPGNGame(game: PGNGame): StringBuilder {
    addPGNGameTags(game.tags)
    append('\n')
    addPGNMoveSectionElements(game.elements)
    append('\n')
    return this
}