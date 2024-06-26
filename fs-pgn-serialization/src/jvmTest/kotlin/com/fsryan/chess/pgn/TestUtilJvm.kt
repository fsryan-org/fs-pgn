package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

actual fun readResourceFile(resource: String): BufferedSource {
    return FileSystem.RESOURCES.openReadOnly(resource.toPath()).source().buffer()
}