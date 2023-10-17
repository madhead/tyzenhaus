package me.madhead.tyzenhaus.repository.postgresql.api.token

import java.net.URI
import java.time.Instant
import java.util.UUID
import me.madhead.tyzenhaus.entity.api.token.APIToken
import me.madhead.tyzenhaus.entity.api.token.Scope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class APITokenRepositoryTest {
    private lateinit var apiTokenRepository: APITokenRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        apiTokenRepository = APITokenRepository(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
                user = databaseUri.userInfo.split(":")[0]
                password = databaseUri.userInfo.split(":")[1]
            }
        )
    }

    @Test
    fun get() {
        assertEquals(
            APIToken(UUID.fromString("00000000-0000-0000-0000-000000000000"), 1, Scope.HISTORY, Instant.ofEpochMilli(253400572800000)),
            apiTokenRepository.get(UUID.fromString("00000000-0000-0000-0000-000000000000"))
        )
    }

    @Test
    fun getNonExisting() {
        assertNull(apiTokenRepository.get(UUID.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF")))
    }

    @Test
    fun save() {
        apiTokenRepository.save(
            APIToken(UUID.fromString("00000000-0000-0000-0000-000000000001"), 2, Scope.HISTORY, Instant.ofEpochMilli(808174800000)),
        )

        assertEquals(
            APIToken(UUID.fromString("00000000-0000-0000-0000-000000000001"), 2, Scope.HISTORY, Instant.ofEpochMilli(808174800000)),
            apiTokenRepository.get(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        )
    }
}
