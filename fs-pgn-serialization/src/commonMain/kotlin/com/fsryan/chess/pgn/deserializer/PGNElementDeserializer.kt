package com.fsryan.chess.pgn.deserializer

import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNNumericAnnotationGlyph
import com.fsryan.chess.pgn.PGNParseException
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.PGNSANMove
import okio.BufferedSource
import okio.use

internal fun BufferedSource.deserializePGNElement(position: Int, moveIsBlack: Boolean): PGNDeserializationResult<PGNGamePly> {
    if (exhausted()) {
        throw PGNParseException(position, "Unexpected end of file while reading PGN element")
    }

    var moveNumber: Int? = null
    var sanMove: PGNSANMove? = null
    var nag: PGNNumericAnnotationGlyph? = null
    var recursiveAnnotationVariation: PGNRecursiveVariationAnnotation? = null
    val comments = mutableListOf<String>()

    var nextPosition = position

    while (true) {
        nextPosition += readWhitespace(nextPosition)
        if (exhausted()) {
            break
        }
        var mustBreak = false
        peek().use { peekable ->
            val nextChar = peekable.readUTF8Char()
            when {
                nextChar.isDigit() -> when (sanMove) {
                    null -> {
                        val moveNumberResult = deserializeMoveNumberIndication(nextPosition)
                        nextPosition += moveNumberResult.charactersRead
                        moveNumber = moveNumberResult.value
                    }
                    else -> mustBreak = true
                }
                nextChar.canStartMove -> when(sanMove) {
                    null -> {
                        val sanMoveResult = deserializeSANMove(nextPosition, moveIsBlack)
                        nextPosition += sanMoveResult.charactersRead
                        sanMove = sanMoveResult.value
                    }
                    else -> mustBreak = true
                }
                nextChar.isNAGStart -> when (nag) {
                    null -> {
                        incrememtByUTF8Char()
                        val nagResult = deserializeNumericAnnotationGlyph(nextPosition)
                        nextPosition += nagResult.charactersRead + 1
                        nag = nagResult.value
                    }
                    else -> mustBreak = true
                }
                nextChar.isRecursiveAnnotationVariationStart -> {
                    incrememtByUTF8Char()
                    val recursiveVariationResult = deserializeRecursiveVariation(nextPosition, firstMoveIsBlack = moveIsBlack)
                    recursiveAnnotationVariation = recursiveVariationResult.value
                    nextPosition += recursiveVariationResult.charactersRead + 1
                }
                nextChar.isEndOfLineCommentaryStart -> {
                    incrememtByUTF8Char()
                    val comment = consumeUTF8CharsAndStopAfter { it.isEndOfLIneCommentaryEnd }
                    comments.add(comment)
                    nextPosition += comment.length + 2  // <-- initial ; plus newline 
                }
                nextChar.isCurlyCommentaryStart -> {
                    incrememtByUTF8Char()
                    val comment = consumeUTF8CharsAndStopAfter { it.isCurlyCommentaryEnd }
                    comments.add(comment)
                    nextPosition += comment.length + 2  // <-- initial { plus }
                }
                else -> mustBreak = true
            }
        }

        if (mustBreak) {
            break
        }
    }

    return sanMove?.let {
        PGNDeserializationResult(
            value = PGNGamePly(
                comments = comments.toList(),
                isBlack = moveIsBlack,
                numberIndicator = moveNumber,
                numericAnnotationGlyph = nag,
                recursiveAnnotationVariation = recursiveAnnotationVariation,
                sanMove = it,
            ),
            charactersRead = nextPosition - position
        )
    } ?: throw PGNParseException(position, "No SAN move found")
}

internal fun BufferedSource.deserializeMoveNumberIndication(position: Int): PGNDeserializationResult<Int> {
    var nextPosition = position + readWhitespace(position)
    val integerResult = readIntegerToken(nextPosition)
    nextPosition += integerResult.charactersRead
    return peek().use { peekableSource ->
        val positionBeforePeriods = nextPosition
        while (true) {
            if (peekableSource.exhausted()) {
                break
            }

            var breakOutOf = false
            peekableSource.readExpectedUTF8Char(
                expected = '.',
                onDifferentCharRead = { char ->
                    breakOutOf = true
                },
                onIOException = { ioe ->
                    throw PGNParseException(nextPosition, "Unexpected error while reading move number", ioe)
                }
            )
            if (breakOutOf) {
                break
            }
            nextPosition++
        }

        incrementByUTF8CharacterCount(nextPosition - positionBeforePeriods)
        PGNDeserializationResult(nextPosition - position, integerResult.value)
    }
}

internal fun BufferedSource.deserializeNumericAnnotationGlyph(position: Int): PGNDeserializationResult<PGNNumericAnnotationGlyph> {
    if (exhausted()) {
        throw PGNParseException(position, "Unexpected end of file while reading NAG")
    }
    val integerResult = readIntegerToken(position)
    return PGNDeserializationResult(integerResult.charactersRead, PGNNumericAnnotationGlyph.fromId(integerResult.value))
}


internal fun BufferedSource.deserializeRecursiveVariation(
    position: Int,
    firstMoveIsBlack: Boolean
): PGNDeserializationResult<PGNRecursiveVariationAnnotation> {
    val plies = mutableListOf<PGNGamePly>()
    var currentMoveIsBlack = firstMoveIsBlack
    var currentPosition = position
    while (true) {
        currentPosition += readWhitespace(currentPosition)
        if (exhausted()) {
            return PGNDeserializationResult(currentPosition - position, PGNRecursiveVariationAnnotation(plies.toList()))
        }
        peek().use { peekable ->
            val nextChar = peekable.readUTF8Char()
            when {
                nextChar.isRecursiveAnnotationVariationEnd -> {
                    incrememtByUTF8Char()
                    return PGNDeserializationResult(currentPosition - position + 1, PGNRecursiveVariationAnnotation(plies.toList()))
                }
                else -> deserializePGNElement(currentPosition, moveIsBlack = currentMoveIsBlack).let { elementResult ->
                    plies.add(elementResult.value)
                    currentPosition += elementResult.charactersRead
                    currentMoveIsBlack = !currentMoveIsBlack
                }
            }
        }
    }
}