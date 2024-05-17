package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.Path

expect fun readResourceFile(resource: String): BufferedSource