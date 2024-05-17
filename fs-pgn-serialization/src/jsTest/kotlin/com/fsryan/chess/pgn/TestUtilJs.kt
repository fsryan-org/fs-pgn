package com.fsryan.chess.pgn

import okio.BufferedSource
import okio.NodeJsFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

actual fun readResourceFile(resource: String): BufferedSource {
    val path = "../../../../fs-pgn-serialization/src/commonTest/resources/${resource}".toPath()
    return NodeJsFileSystem.openReadOnly(path).source().buffer()
}

