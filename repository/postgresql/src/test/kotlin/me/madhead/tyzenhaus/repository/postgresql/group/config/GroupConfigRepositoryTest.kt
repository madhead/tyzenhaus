package me.madhead.tyzenhaus.repository.postgresql.group.config

import java.net.URI
import java.time.Instant
import java.util.Locale
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class GroupConfigRepositoryTest {
    private lateinit var groupConfigRepository: GroupConfigRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        groupConfigRepository = GroupConfigRepository(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
                user = databaseUri.userInfo.split(":")[0]
                password = databaseUri.userInfo.split(":")[1]
            }
        )
    }

    @Test
    fun get() {
        assertEquals(GroupConfig(1, 1, Instant.ofEpochMilli(808174800000), Locale("en")), groupConfigRepository.get(1))
        assertEquals(GroupConfig(2, 2, Instant.ofEpochMilli(808174800000), null), groupConfigRepository.get(2))
        assertEquals(GroupConfig(3, 3, null, null), groupConfigRepository.get(3))
        assertEquals(GroupConfig(4, null, null, null), groupConfigRepository.get(4))
        assertEquals(
            GroupConfig(5, 1, Instant.ofEpochMilli(808174800000), Locale("en"), setOf(1, 2, 3)),
            groupConfigRepository.get(5)
        )
    }

    @Test
    fun getNonExisting() {
        assertNull(groupConfigRepository.get(0))
    }

    @Test
    fun save() {
        groupConfigRepository.save(GroupConfig(-1, -1, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)))

        assertEquals(
            GroupConfig(-1, -1, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)),
            groupConfigRepository.get(-1)
        )
    }

    @Test
    fun update() {
        groupConfigRepository.save(GroupConfig(-2))

        assertEquals(GroupConfig(-2), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2))

        assertEquals(GroupConfig(-2, -2), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000)))

        assertEquals(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000)), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by")))

        assertEquals(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by")), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)))

        assertEquals(
            GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)),
            groupConfigRepository.get(-2)
        )
    }
}
