package com.fsryan.chess.pgn.ktor

import io.ktor.http.*

/**
 * Extension to provide the `application/vnd.chess-pgn` [ContentType].
 */
val ContentType.Application.PGN: ContentType
    get() = ContentType("application", "vnd.chess-pgn")