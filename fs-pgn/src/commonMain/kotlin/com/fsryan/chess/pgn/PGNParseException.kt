package com.fsryan.chess.pgn

open class PGNParseException(val position: Int, message: String, cause: Throwable? = null): Exception(message, cause)

open class PGNStringException(position: Int, message: String): PGNParseException(position, message)
class PGNStringControlCharacterFoundException(position: Int, val char: Char): PGNStringException(
    position = position,
    message = "Unexpected control character found while reading string"
)
class PGNCannotEscapeCharacterException(position: Int, val char: Char): PGNStringException(
    position = position,
    message = "Unexpected character found while attempting to read escaped character in string"
)

open class PGNSymbolException(position: Int, message: String): PGNParseException(position, message)
class PGNSymbolTooLongException(position: Int): PGNSymbolException(
    position = position,
    message = "Symbol is limited to a maximum of 255 characters in length"
)

class PGNIllegalSymbolStartingCharacterException(position: Int, val char: Char): PGNSymbolException(
    position = position,
    message = "Symbol must start with a letter or digit"
)


open class PGNTagPairException(position: Int, message: String): PGNParseException(position, message)

class PGNUnexpectedTagTerminator(position: Int, val char: Char): PGNTagPairException(
    position = position,
    message = "Expected ']' to close tag pair"
)

class PGNUnexpectedTagValueDelimiter(position: Int, val char: Char): PGNTagPairException(
    position = position,
    message = "Expected '\"' to start tag value"
)

class PGNDuplicateTagException(position: Int, val key: String): PGNTagPairException(
    position = position,
    message = "Duplicate tag"
)

open class PGNMoveTextException(position: Int, message: String): PGNParseException(position, message)

class PGNUnexpectedMoveTextDelimiter(position: Int, val char: Char): PGNMoveTextException(
    position = position,
    message = "Unexpected character found while reading move text"
)


open class PGNIntegerException(position: Int, message: String): PGNParseException(position, message)

class PGNInvalidIntegerStartingCharException(position: Int, val char: Char): PGNIntegerException(
    position = position,
    message = "Expected non-zero digit character but found '$char'"
)