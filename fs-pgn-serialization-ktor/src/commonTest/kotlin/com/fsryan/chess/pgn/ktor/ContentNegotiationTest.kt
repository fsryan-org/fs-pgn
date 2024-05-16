package com.fsryan.chess.pgn.ktor

import com.fsryan.chess.pgn.PGNGameDatabase
import com.fsryan.chess.pgn.deserializer.deserializePGNGameDatabase
import com.fsryan.chess.pgn.serializer.addPGNGameDatabase
import com.fsryan.chess.pgn.test.TestPGNGameDatabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import okio.Buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentNegotiationTest {

    @Test
    fun shouldSerialize() = runTest {
        val gameDatabase = TestPGNGameDatabase()
        val mockEngine = MockEngine { request ->
            assertEquals(ContentType.parse("application/vnd.chess-pgn"), request.body.contentType)
            val sent = Buffer().use { buf ->
                buf.write(request.body.toByteArray())
                buf.deserializePGNGameDatabase(0)
            }
            assertEquals(gameDatabase, sent.value)
            respond(
                content = "",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/vnd.chess-pgn")
            )
        }
        val testApiClient = TestApiClient(mockEngine)
        val response = testApiClient.post(gameDatabase)
        println(response)
    }

    @Test
    fun shouldDeserialize() = runTest {
        val gameDatabase = TestPGNGameDatabase()
        val mockEngine = MockEngine { request ->
            respond(
                content = StringBuilder().addPGNGameDatabase(gameDatabase).toString(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/vnd.chess-pgn")
            )
        }
        val testApiClient = TestApiClient(mockEngine)
        val actual = testApiClient.fetch()
        assertEquals(gameDatabase, actual)
    }

}

class TestApiClient(engine: HttpClientEngine) {
    private val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            fsPGNSerialization()
        }
    }

    suspend fun post(db: PGNGameDatabase): HttpResponse = httpClient.post("https://example.com") {
        contentType(ContentType.parse("application/vnd.chess-pgn"))
        setBody(db)
    }
    suspend fun fetch(): PGNGameDatabase = httpClient.get("https://api.ipify.org/?format=json").body()
}