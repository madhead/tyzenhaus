package me.madhead.tyzenhaus.repository.postgresql.group.config

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import java.time.Instant
import java.util.Locale

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class GroupConfigRepositoryTest {
    lateinit var groupConfigRepository: GroupConfigRepository

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
        Assertions.assertEquals(GroupConfig(1, 1, Instant.ofEpochMilli(808174800000), Locale("en")), groupConfigRepository.get(1))
        Assertions.assertEquals(GroupConfig(2, 2, Instant.ofEpochMilli(808174800000), null), groupConfigRepository.get(2))
        Assertions.assertEquals(GroupConfig(3, 3, null, null), groupConfigRepository.get(3))
        Assertions.assertEquals(GroupConfig(4, null, null, null), groupConfigRepository.get(4))
        Assertions.assertEquals(
            GroupConfig(5, 1, Instant.ofEpochMilli(808174800000), Locale("en"), setOf(1, 2, 3)),
            groupConfigRepository.get(5)
        )
    }

    @Test
    fun getNonExisting() {
        Assertions.assertNull(groupConfigRepository.get(0))
    }

    @Test
    fun save() {
        groupConfigRepository.save(GroupConfig(-1, -1, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)))

        Assertions.assertEquals(
            GroupConfig(-1, -1, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)),
            groupConfigRepository.get(-1)
        )
    }

    @Test
    fun update() {
        groupConfigRepository.save(GroupConfig(-2))

        Assertions.assertEquals(GroupConfig(-2), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2))

        Assertions.assertEquals(GroupConfig(-2, -2), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000)))

        Assertions.assertEquals(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000)), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by")))

        Assertions.assertEquals(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by")), groupConfigRepository.get(-2))

        groupConfigRepository.save(GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)))

        Assertions.assertEquals(
            GroupConfig(-2, -2, Instant.ofEpochMilli(808174800000), Locale("by"), setOf(1, 2, 3)),
            groupConfigRepository.get(-2)
        )
    }
}
