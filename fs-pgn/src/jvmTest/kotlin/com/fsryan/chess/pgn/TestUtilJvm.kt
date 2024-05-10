package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.FileSystem
import okio.Path
import okio.buffer

actual fun readResourceFile(path: Path): BufferedSource {
    return FileSystem.RESOURCES.openReadOnly(path).source().buffer()
}