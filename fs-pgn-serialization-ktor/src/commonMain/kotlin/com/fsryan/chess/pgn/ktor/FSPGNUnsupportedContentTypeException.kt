package com.fsryan.chess.pgn.ktor

import io.ktor.http.*
import kotlin.reflect.KType

class FSPGNUnsupportedContentTypeException(val contentType: ContentType): Exception("Unsupported content type: $contentType")

class FSPGNUnsupportedObjectTypeException(val objectType: KType?): Exception("Unsupported object type: $objectType")