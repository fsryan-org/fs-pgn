package com.fsryan.chess.pgn.serializer

import com.fsryan.chess.pgn.PGNCastle
import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.pgnString
import com.fsryan.chess.pgn.test.TestPGNSquare
import com.fsryan.chess.pgn.test.TestSimplePGNSANMove
import com.fsryan.chess.pgn.test.allFiles
import com.fsryan.chess.pgn.test.allRanks
import com.fsryan.chess.pgn.test.randomOptional
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class PGNSanMoveSerializationTest {

    @Test
    fun shouldCorrectlyCreatePGNStringOfPawnMove() {
        val input = TestSimplePGNSANMove(piece = PGNGamePiece.Pawn)
        val expected = input.destination.pgnString
        val actual = input.pgnString
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfPawnMoveWithCheckStatus() {
        sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
            val input = TestSimplePGNSANMove(piece = PGNGamePiece.Pawn, checkStatus = checkStatus)
            val expected = "${input.destination.pgnString}${checkStatus.serialValue}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfDisambiguatedPawnMove() {
        allFiles().forEach { destinationFile ->
            allFiles().filter {
                abs(it.code - destinationFile.code) == 1
            }.forEach { sourceFile ->
                val input = TestSimplePGNSANMove(
                    piece = PGNGamePiece.Pawn,
                    sourceFile = sourceFile,
                    isCapture = true,
                    destination = TestPGNSquare(file = destinationFile)
                )
                val expected = "${sourceFile}x${input.destination.pgnString}"
                val actual = input.pgnString
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfDisambiguatedPawnMoveWithCheckStatus() {
        sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
            allFiles().forEach { destinationFile ->
                allFiles().filter {
                    abs(it.code - destinationFile.code) == 1
                }.forEach { sourceFile ->
                    val input = TestSimplePGNSANMove(
                        piece = PGNGamePiece.Pawn,
                        sourceFile = sourceFile,
                        isCapture = true,
                        destination = TestPGNSquare(file = destinationFile),
                        checkStatus = checkStatus
                    )
                    val expected = "${sourceFile}x${input.destination.pgnString}${checkStatus.serialValue}"
                    val actual = input.pgnString
                    assertEquals(expected, actual)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfPawnPromotionMove() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { promotionPiece ->
            val input = TestSimplePGNSANMove(
                piece = PGNGamePiece.Pawn,
                promotionPiece = promotionPiece,
                destination = TestPGNSquare(rank = randomOptional { 1 } ?: 8)
            )
            val expected = "${input.destination.pgnString}=${promotionPiece.serialValue}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfPawnPromotionMoveWithCapture() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { promotionPiece ->
            allFiles().forEach { sourceFile ->
                val input = TestSimplePGNSANMove(
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = promotionPiece,
                    isCapture = true,
                    destination = TestPGNSquare(rank = randomOptional { 1 } ?: 8),
                    sourceFile = sourceFile
                )
                val expected = "${sourceFile}x${input.destination.pgnString}=${promotionPiece.serialValue}"
                val actual = input.pgnString
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfPawnPromotionMoveWithCaptureWithCheckStatus() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { promotionPiece ->
            sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
                allFiles().forEach { sourceFile ->
                    val input = TestSimplePGNSANMove(
                        piece = PGNGamePiece.Pawn,
                        promotionPiece = promotionPiece,
                        isCapture = true,
                        destination = TestPGNSquare(rank = randomOptional { 1 } ?: 8),
                        sourceFile = sourceFile,
                        checkStatus = checkStatus
                    )
                    val expected = "${sourceFile}x${input.destination.pgnString}=${promotionPiece.serialValue}${checkStatus.serialValue}"
                    val actual = input.pgnString
                    assertEquals(expected, actual)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfNonPawnMove() {
        PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
            val input = TestSimplePGNSANMove(piece = piece)
            val expected = "${piece.serialValue}${input.destination.pgnString}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfNonPawnMoveWithCapture() {
        PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
            val input = TestSimplePGNSANMove(piece = piece, isCapture = true)
            val expected = "${piece.serialValue}x${input.destination.pgnString}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfNonPawnMoveWithCaptureWithCheckStatus() {
        PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
            sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
                val input = TestSimplePGNSANMove(piece = piece, isCapture = true, checkStatus = checkStatus)
                val expected = "${piece.serialValue}x${input.destination.pgnString}${checkStatus.serialValue}"
                val actual = input.pgnString
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfNonPawnMoveWithCheckStatus() {
        PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
            sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
                val input = TestSimplePGNSANMove(piece = piece, checkStatus = checkStatus)
                val expected = "${piece.serialValue}${input.destination.pgnString}${checkStatus.serialValue}"
                val actual = input.pgnString
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfFileOnlyDisambiguatedNonPawnMove() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allFiles().forEach { destinationFile ->
                allFiles().forEach { sourceFile ->
                    PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
                        val input = TestSimplePGNSANMove(
                            piece = piece,
                            sourceFile = sourceFile,
                            destination = TestPGNSquare(file = destinationFile)
                        )
                        val expected = "${piece.serialValue}${sourceFile}${input.destination.pgnString}"
                        val actual = input.pgnString
                        assertEquals(expected, actual)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfFileOnlyDisambiguatedNonPawnMoveWithCapture() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allFiles().forEach { destinationFile ->
                allFiles().forEach { sourceFile ->
                    PGNGamePiece.entries.filter { it != PGNGamePiece.Pawn }.forEach { piece ->
                        val input = TestSimplePGNSANMove(
                            piece = piece,
                            sourceFile = sourceFile,
                            isCapture = true,
                            destination = TestPGNSquare(file = destinationFile)
                        )
                        val expected = "${piece.serialValue}${sourceFile}x${input.destination.pgnString}"
                        val actual = input.pgnString
                        assertEquals(expected, actual)
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfRankOnlyDisambiguatedNonPawnMove() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allRanks().forEach { destinationRank ->
                allRanks().forEach { sourceRank ->
                    val input = TestSimplePGNSANMove(
                        piece = piece,
                        sourceRank = sourceRank,
                        isCapture = false,
                        destination = TestPGNSquare(rank = destinationRank)
                    )
                    val expected = "${piece.serialValue}${sourceRank}${input.destination.pgnString}"
                    val actual = input.pgnString
                    assertEquals(expected, actual)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfRankOnlyDisambiguatedNonPawnMoveWithCapture() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allRanks().forEach { destinationRank ->
                allRanks().forEach { sourceRank ->
                    val input = TestSimplePGNSANMove(
                        piece = piece,
                        sourceRank = sourceRank,
                        isCapture = true,
                        destination = TestPGNSquare(rank = destinationRank)
                    )
                    val expected = "${piece.serialValue}${sourceRank}x${input.destination.pgnString}"
                    val actual = input.pgnString
                    assertEquals(expected, actual)
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfFileAndRankDisambiguatedNonPawnMove() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allFiles().forEach { destinationFile ->
                allFiles().forEach { sourceFile ->
                    allRanks().forEach { destinationRank ->
                        allRanks().forEach { sourceRank ->
                            val input = TestSimplePGNSANMove(
                                piece = piece,
                                sourceFile = sourceFile,
                                sourceRank = sourceRank,
                                isCapture = false,
                                destination = TestPGNSquare(file = destinationFile, rank = destinationRank)
                            )
                            val expected = "${piece.serialValue}${sourceFile}${sourceRank}${input.destination.pgnString}"
                            val actual = input.pgnString
                            assertEquals(expected, actual)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreatePGNStringOfFileAndRankDisambiguatedNonPawnMoveWithCapture() {
        sequenceOf(PGNGamePiece.Queen, PGNGamePiece.Rook, PGNGamePiece.Knight, PGNGamePiece.Bishop).forEach { piece ->
            allFiles().forEach { destinationFile ->
                allFiles().forEach { sourceFile ->
                    allRanks().forEach { destinationRank ->
                        allRanks().forEach { sourceRank ->
                            val input = TestSimplePGNSANMove(
                                piece = piece,
                                sourceFile = sourceFile,
                                sourceRank = sourceRank,
                                isCapture = true,
                                destination = TestPGNSquare(file = destinationFile, rank = destinationRank)
                            )
                            val expected = "${piece.serialValue}${sourceFile}${sourceRank}x${input.destination.pgnString}"
                            val actual = input.pgnString
                            assertEquals(expected, actual)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun shouldCorrectlyCreateKingSideCastle() {
        val input = TestSimplePGNSANMove(castleType = PGNCastle.KingSide)
        val expected = "O-O"
        val actual = input.pgnString
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyCreateKingSideCastleWithCheckStatus() {
        sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
            val input = TestSimplePGNSANMove(castleType = PGNCastle.KingSide, checkStatus = checkStatus)
            val expected = "O-O${checkStatus.serialValue}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }

    @Test
    fun shouldCorrectlyCreateQueenSideCastle() {
        val input = TestSimplePGNSANMove(castleType = PGNCastle.QueenSide)
        val expected = "O-O-O"
        val actual = input.pgnString
        assertEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyCreateQueenSideCastleWithCheckStatus() {
        sequenceOf(PGNCheckStatus.Check, PGNCheckStatus.Checkmate).forEach { checkStatus ->
            val input = TestSimplePGNSANMove(castleType = PGNCastle.QueenSide, checkStatus = checkStatus)
            val expected = "O-O-O${checkStatus.serialValue}"
            val actual = input.pgnString
            assertEquals(expected, actual)
        }
    }
}