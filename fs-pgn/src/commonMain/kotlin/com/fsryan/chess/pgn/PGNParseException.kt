package com.fsryan.chess.pgn

open class PGNParseException(val position: Int, message: String, cause: Throwable? = null): Exception(message, cause)

class PGNUnescapbleCharacterException(position: Int, val char: Char, message: String): PGNParseException(position, message)