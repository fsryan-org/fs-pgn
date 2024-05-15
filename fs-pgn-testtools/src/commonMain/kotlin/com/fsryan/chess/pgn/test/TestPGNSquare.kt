package com.fsryan.chess.pgn.test

import com.fsryan.chess.pgn.PGNSquare

fun TestPGNSquare(file: Char = randomFile(), rank: Int = randomRank()): PGNSquare = PGNSquare(file = file, rank = rank)
fun allFiles(): List<Char> = ('a' .. 'h').toList()
fun randomFile(): Char = ('a' .. 'h').random()
fun allRanks(): List<Int> = (1 .. 8).toList()
fun randomRank(): Int = (1 .. 8).random()