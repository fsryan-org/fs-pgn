package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNGameResult
import com.fsryan.chess.pgn.PGNMoveSectionElement
import com.fsryan.chess.pgn.PGNRecursiveVariationAnnotation
import com.fsryan.chess.pgn.comments
import com.fsryan.chess.pgn.pgnString
import com.fsryan.chess.pgn.plies


internal fun StringBuilder.addPGNMoveSectionElements(
    elements: List<PGNMoveSectionElement>,
    initialMoveNumber: Int = 1
): StringBuilder {
    elements.forEachIndexed { index, element ->
        addPGNMoveSectionElement(
            moveNumber = initialMoveNumber + index / 2,
            element = element,
            prependSeparator = index != 0
        )
    }
    return this
}

internal fun StringBuilder.addPGNMoveSectionElement(moveNumber: Int, element: PGNMoveSectionElement, prependSeparator: Boolean): StringBuilder {
    when (element) {
        is PGNGamePly -> addMoveSectionPly(
            ply = element,
            moveNumber = moveNumber,
            prependSeparator = prependSeparator
        )
        is PGNGameResult -> addMoveSectionResult(result = element, prependSeparator = prependSeparator)
    }
    return this
}

internal fun StringBuilder.addMoveSectionResult(result: PGNGameResult, prependSeparator: Boolean): StringBuilder {
    addMoveSectionString(string = result.result.serialValue, prependSeparator = prependSeparator)
    return this
}

internal fun StringBuilder.addMoveSectionPly(
    ply: PGNGamePly,
    moveNumber: Int,
    prependSeparator: Boolean,
    serializeMoveNumber: Boolean = !ply.isBlack,
): StringBuilder {
    var shouldPrependSeparator = prependSeparator
    if (serializeMoveNumber) {
        addMoveSectionString(
            string = "$moveNumber${if (ply.isBlack) "..." else "."}",
            prependSeparator = shouldPrependSeparator
        )
        shouldPrependSeparator = true
    }

    addMoveSectionString(string = ply.sanMove.pgnString, prependSeparator = shouldPrependSeparator)

    ply.numericAnnotationGlyph?.let { addMoveSectionString(string = it.pgnString, prependSeparator = true) }

    ply.comments.forEach { comment ->
        addMoveSectionString(string = "{$comment}", prependSeparator = true)
    }

    ply.recursiveAnnotationVariation?.let {
        addRecursiveAnnotationVariation(variation = it, firstMoveNumber = moveNumber, firstMoveIsBlack = ply.isBlack)
    }

    return this
}

internal fun StringBuilder.addRecursiveAnnotationVariation(
    variation: PGNRecursiveVariationAnnotation,
    firstMoveNumber: Int,
    firstMoveIsBlack: Boolean
): StringBuilder {
    addMoveSectionString("(", prependSeparator = true)
    variation.plies.forEachIndexed { index, ply ->
        val serializeMoveNumber = index == 0 || !ply.isBlack
        val moveNumberOffset = if (firstMoveIsBlack) (index + 1) / 2 else index / 2
        addMoveSectionPly(
            ply = ply,
            moveNumber = firstMoveNumber + moveNumberOffset,
            prependSeparator = index != 0,
            serializeMoveNumber = serializeMoveNumber
        )
    }
    return addMoveSectionString(")", prependSeparator = false)
}

private fun StringBuilder.addMoveSectionString(string: String, prependSeparator: Boolean): StringBuilder {
    val lastNewlineIndex = lastIndexOf('\n')
    val currentLineLength = length - if (lastNewlineIndex < 0) 0 else lastNewlineIndex
    var resultingLength = currentLineLength + string.length
    if (prependSeparator) {
        resultingLength++
        append(if (resultingLength > 80) '\n' else ' ')
    } else if (resultingLength > 80) {
        append('\n')
    }
    return append(string)
}