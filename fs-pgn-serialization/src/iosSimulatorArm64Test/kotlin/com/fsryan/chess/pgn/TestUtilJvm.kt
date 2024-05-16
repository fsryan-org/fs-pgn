package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.FileSystem
import okio.Path
import okio.buffer

actual fun readResourceFile(path: Path): BufferedSource {
    // TODO: make this work
    return FileSystem.SYSTEM.openReadOnly(path).source().buffer()
}