package me.madhead.tyzenhaus.repository.postgresql.api.token

import java.time.Instant
import java.util.UUID
import me.madhead.tyzenhaus.entity.api.token.APIToken
import me.madhead.tyzenhaus.entity.api.token.Scope
import me.madhead.tyzenhaus.repository.postgresql.AbstractRepositoryTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class APITokenRepositoryTest : AbstractRepositoryTest() {
    private lateinit var apiTokenRepository: APITokenRepository

    @BeforeAll
    fun setUp() {
        apiTokenRepository = APITokenRepository(dataSource)
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
