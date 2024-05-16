package com.fsryan.chess.pgn.ktor

import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.deserializer.deserializePGNGameDatabase
import com.fsryan.chess.pgn.serializer.addPGNGameDatabase
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.core.*
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.use

internal class FSPGNSerializationConverter: ContentConverter {

    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        if (contentType.contentType != ContentType.Application.PGN.contentType
            || contentType.contentSubtype != ContentType.Application.PGN.contentSubtype) {
            throw FSPGNUnsupportedContentTypeException(contentType)
        }

        if (value !is PGNGameDatabase?) {
            throw FSPGNUnsupportedObjectTypeException(typeInfo.kotlinType)
        }

        return value?.let { db ->
            val serialized = StringBuilder().addPGNGameDatabase(db).toString()
            TextContent(serialized, contentType.withCharsetIfNeeded(charset))
        }
    }

    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        val contentPacket = content.readRemaining()
        Buffer().use { buf ->
            val contentString = contentPacket.readText(charset)
            buf.write(contentString.encodeUtf8())
            val result = buf.deserializePGNGameDatabase()
            return result.value
        }
    }
}

/**
 * Register FSPGN.serialization converter into [ContentNegotiation] plugin
 */
fun Configuration.fsPGNSerialization() {
    register(ContentType.Application.PGN, FSPGNSerializationConverter())
}