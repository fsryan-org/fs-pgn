package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNGameTags
import com.fsryan.chess.pgn.keys

internal fun StringBuilder.addPGNGameTags(tags: PGNGameTags): StringBuilder {
    tags.keys.forEach { key ->
        tags.valueOf(key)?.let { value ->
            addPGNTagPair(key, value)
            append('\n')
        }
    }
    return this
}

internal fun StringBuilder.addPGNTagPair(key: String, value: String): StringBuilder {
    append('[')
    append(key)
    append(" \"")
    append(value)
    return append("\"]")
}