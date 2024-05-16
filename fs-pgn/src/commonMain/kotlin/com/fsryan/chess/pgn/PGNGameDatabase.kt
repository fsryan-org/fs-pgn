@file:OptIn(ExperimentalJsExport::class)

package com.fsryan.chess.pgn

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface PGNGameDatabase {
    /**
     * All games in the standard collating sequence
     */
    val allArray: Array<PGNGame>
}

fun PGNGameDatabase(
    all: List<PGNGame>
): PGNGameDatabase = PGNGameDatabaseData(sortStandardCollatingSequence(all))

@JsExport
fun createPGNGameDatabase(allArray: Array<PGNGame>): PGNGameDatabase = PGNGameDatabase(allArray.toList())

val PGNGameDatabase.all: List<PGNGame>
    get() = when (this) {
        is PGNGameDatabaseData -> _all
        else -> allArray.toList()
    }

// Do indexing here
private data class PGNGameDatabaseData(internal val _all: List<PGNGame>): PGNGameDatabase {
    override val allArray: Array<PGNGame>
        get() = _all.toTypedArray()
}

private fun sortStandardCollatingSequence(all: List<PGNGame>): List<PGNGame> {
    return all.sortedWith { g1, g2 ->
        // Sort order 1: Date
        val yearDiff = g1.sortingDateYear - g2.sortingDateYear
        if (yearDiff != 0) {
            return@sortedWith yearDiff
        }
        val monthDiff = g1.sortingDateMonthOfYear - g2.sortingDateMonthOfYear
        if (monthDiff != 0) {
            return@sortedWith monthDiff
        }
        val dayDiff = g1.sortingDateDayOfMonth - g2.sortingDateDayOfMonth
        if (dayDiff != 0) {
            return@sortedWith dayDiff
        }

        // Sort order 2: Event
        val eventComparison = g1.tags.event.compareTo(g2.tags.event)
        if (eventComparison != 0) {
            return@sortedWith eventComparison
        }

        // Sort order 3: Site
        val siteComparison = g1.tags.site.compareTo(g2.tags.site)
        if (siteComparison != 0) {
            return@sortedWith siteComparison
        }

        // Sort order 4: Round
        val roundComparison = g1.tags.roundInt?.let { r1 ->
            g2.tags.roundInt?.let { r2 ->
                r1 - r2
            }
        } ?: g2.tags.roundInt?.let { -1 } ?: 0

        // Sort order 5: White
        val whiteComparison = g1.tags.white.compareTo(g2.tags.white)
        if (whiteComparison != 0) {
            return@sortedWith whiteComparison
        }

        // Sort order 6: Black
        val blackComparison = g1.tags.black.compareTo(g2.tags.black)
        if (blackComparison != 0) {
            return@sortedWith blackComparison
        }

        // Sort order 7: Result
        val resultComparison = g1.tags.result.serialValue.compareTo(g2.tags.result.serialValue)
        if (resultComparison != 0) {
            return@sortedWith resultComparison
        }

        // Sort order 8: Move Text
        val g1MoveText = g1.elements.map { element ->
            when (element) {
                is PGNGamePly -> with(element) {
                    val moveNumber = numberIndicator?.let { "$it${if (isBlack) "..." else "."}" } ?: ""
                    val nag = numericAnnotationGlyph?.let { "\$${it.id}" } ?: ""
                    val san = sanMove.pgnString
                    "$moveNumber$san$nag${comments.map { "{$it}" }.joinToString("")}"
                }
                is PGNGameResult -> element.result.serialValue
                else -> ""
            }
        }.joinToString(" ")
        val g2MoveText = g2.elements.map { element ->
            when (element) {
                is PGNGamePly -> with(element) {
                    val moveNumber = numberIndicator?.let { "$it${if (isBlack) "..." else "."}" } ?: ""
                    val nag = numericAnnotationGlyph?.let { "\$${it.id}" } ?: ""
                    val san = sanMove.pgnString
                    "$moveNumber$san$nag${comments.map { "{$it}" }.joinToString("")}"
                }
                is PGNGameResult -> element.result.serialValue
                else -> ""
            }
        }.joinToString(" ")
        g1MoveText.compareTo(g2MoveText)
    }
}