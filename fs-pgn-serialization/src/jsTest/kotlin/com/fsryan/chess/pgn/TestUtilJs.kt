package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.NodeJsFileSystem
import okio.Path
import okio.buffer

actual fun readResourceFile(path: Path): BufferedSource {
    return NodeJsFileSystem.openReadOnly(path).source().buffer()
}