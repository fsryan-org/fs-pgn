package com.fsryan.chess.pgn.parser

/**
 * A symbol token starts with a letter or digit character
 *
 * @see isValidSymbolContinuationChar
 * @see okio.BufferedSource.readPGNSymbolToken
 */
internal val Char.isValidStartingSymbolChar: Boolean get() = when {
    code < 48 -> false
    code in 58..64 -> false
    code in 91..96 -> false
    code > 122 -> false
    else -> true
}

/**
 * A symbol token starts with a letter or digit character and is immediately
 * followed by a sequence of zero or more symbol continuation characters.
 * These continuation characters are letter characters ("A-Za-z"), digit
 * characters ("0-9"), the underscore ("_"), the plus sign ("+"), the
 * octothorpe sign ("#"), the equal sign ("="), the colon (":"), and the hyphen
 * ("-").
 */
internal val Char.isValidSymbolContinuationChar: Boolean get() = when {
    isValidStartingSymbolChar -> true
    this == '_' -> true
    this == '+' -> true
    this == '#' -> true
    this == '=' -> true
    this == ':' -> true
    this == '-' -> true
    else -> false
}

internal val Char.isPawn: Boolean get() = this == 'P'
internal val Char.isKnight: Boolean get() = this == 'N'
internal val Char.isBishop: Boolean get() = this == 'B'
internal val Char.isRook: Boolean get() = this == 'R'
internal val Char.isQueen: Boolean get() = this == 'Q'
internal val Char.isKing: Boolean get() = this == 'K'
internal val Char.isPiece: Boolean get() = isPawn || isKnight || isBishop || isRook || isQueen || isKing
internal val Char.isPossiblePromotionPiece get() = isKnight || isBishop || isRook || isQueen

internal val Char.isFile: Boolean get() = this in 'a'..'h'
internal val Char.isRank: Boolean get() = this in '1'..'8'

internal val Char.canStartMove: Boolean get() = isPiece || isFile || isCastleMarker

internal val Char.isCastleMarker: Boolean get() = this == 'O'
internal val Char.isCapture: Boolean get() = this == 'x'
internal val Char.isCheck: Boolean get() = this == '+'
internal val Char.isCheckmate: Boolean get() = this == '#'
internal val Char.isCheckStatus: Boolean get() = isCheck || isCheckmate
internal val Char.isPromotion: Boolean get() = this == '='
internal val Char.isSuffixAnnotationSymbol: Boolean get() = this == '!' || this == '?'

internal val Char.isNAGStart: Boolean get() = this == '$'

internal val Char.isEndOfLineCommentaryStart: Boolean get() = this == ';'
internal val Char.isEndOfLIneCommentaryEnd: Boolean get() = this == '\n'
internal val Char.isCurlyCommentaryStart: Boolean get() = this == '{'
internal val Char.isCurlyCommentaryEnd: Boolean get() = this == '}'

internal val Char.isRecursiveAnnotationVariationStart: Boolean get() = this == '('
internal val Char.isRecursiveAnnotationVariationEnd: Boolean get() = this == ')'