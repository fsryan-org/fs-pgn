package com.fsryan.chess.pgn.parser

import com.fsryan.chess.pgn.PGNCheckStatus
import com.fsryan.chess.pgn.PGNGamePiece
import com.fsryan.chess.pgn.PGNGamePly
import com.fsryan.chess.pgn.PGNSANMove
import com.fsryan.chess.pgn.PGNSquare
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PGNMoveTextSectionParserTest {

    internal val parserUnderTest = PGNMoveTextSectionParser(
        elementParser = { moveIsBlack -> PGNElementParser(sanMoveParser = PGNSANMoveParser(moveIsBlack = moveIsBlack)) }
    )

    @Test
    fun shouldReturnEmptyListWhenEmpty() {
        Buffer().use { buf ->
            buf.write("".encodeUtf8())
            val result = parserUnderTest.parse(buf, 0)
            assertTrue(result.value.isEmpty())
            assertEquals(0, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnSingleElement() {
        Buffer().use { buf ->
            val input = "1. e4"
            buf.write(input.encodeUtf8())
            val result = parserUnderTest.parse(buf, 0)
            assertEquals(1, result.value.size)
            assertEquals(1, (result.value[0] as PGNGamePly).numberInditcator)
            assertEquals(
                PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                ),
                (result.value[0] as PGNGamePly).sanMove
            )
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnMultiplePlyElements() {
        Buffer().use { buf ->
            val input = "1. e4 e5 2. Nf3 Nc6"
            buf.write(input.encodeUtf8())
            val result = parserUnderTest.parse(buf, 0)
            assertEquals(4, result.value.size)
            assertEquals(1, (result.value[0] as PGNGamePly).numberInditcator)
            assertEquals(
                PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 4),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                ),
                (result.value[0] as PGNGamePly).sanMove
            )
            assertEquals(
                PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'e', rank = 5),
                    piece = PGNGamePiece.Pawn,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                ),
                (result.value[1] as PGNGamePly).sanMove
            )
            assertEquals(2, (result.value[2] as PGNGamePly).numberInditcator)
            assertEquals(
                PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'f', rank = 3),
                    piece = PGNGamePiece.Knight,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                ),
                (result.value[2] as PGNGamePly).sanMove
            )
            assertEquals(
                PGNSANMove(
                    castleType = null,
                    checkStatus = PGNCheckStatus.None,
                    destination = PGNSquare(file = 'c', rank = 6),
                    piece = PGNGamePiece.Knight,
                    promotionPiece = null,
                    isCapture = false,
                    sourceFileASCII = null,
                    sourceRank = null,
                    suffixAnnotation = null
                ),
                (result.value[3] as PGNGamePly).sanMove
            )
            assertEquals(input.length, result.charactersRead)
        }
    }

    @Test
    fun shouldReturnMultiplePlyElementsFromATypicalMoveText() {
        Buffer().use { buf ->
            val input = """
                1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3
                O-O 9. h3 Nb8 10. d4 Nbd7 11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15.
                Nb1 h6 16. Bh4 c5 17. dxe5 Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21.
                Nc4 Nxc4 22. Bxc4 Nb6 23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7
                27. Qe3 Qg5 28. Qxg5 hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33.
                f3 Bc8 34. Kf2 Bf5 35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5
                40. Rd6 Kc5 41. Ra6 Nf2 42. g4 Bd3 43. Re6 1/2-1/2
            """.trimIndent()
            buf.write(input.encodeUtf8())
            val result = parserUnderTest.parse(buf, 0)
            assertEquals(86, result.value.size)
        }
    }
}