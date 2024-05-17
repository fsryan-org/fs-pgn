package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

actual fun readResourceFile(resource: String): BufferedSource {
    // TODO: make this work
    FileSystem.SYSTEM.listRecursively(".".toPath()).forEach { println("file: $it") }
    return FileSystem.SYSTEM.openReadOnly(resource.toPath()).source().buffer()
}