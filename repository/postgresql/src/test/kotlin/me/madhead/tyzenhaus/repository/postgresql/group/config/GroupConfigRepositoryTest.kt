package me.madhead.tyzenhaus.repository.postgresql.group.config

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
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
        Assertions.assertEquals(GroupConfig(1, Locale("en")), groupConfigRepository.get(1))
        Assertions.assertEquals(GroupConfig(2, null), groupConfigRepository.get(2))
    }

    @Test
    fun getNonExisting() {
        Assertions.assertNull(groupConfigRepository.get(0))
    }

    @Test
    fun save() {
        groupConfigRepository.save(GroupConfig(-1, Locale("by")))

        Assertions.assertEquals(GroupConfig(-1, Locale("by")), groupConfigRepository.get(-1))
    }

    @Test
    fun saveNullLanguage() {
        groupConfigRepository.save(GroupConfig(-2))

        Assertions.assertEquals(GroupConfig(-2), groupConfigRepository.get(-2))
    }

    @Test
    fun update() {
        groupConfigRepository.save(GroupConfig(-3, Locale("en")))

        Assertions.assertEquals(GroupConfig(-3, Locale("en")), groupConfigRepository.get(-3))

        groupConfigRepository.save(GroupConfig(-3, Locale("by")))

        Assertions.assertEquals(GroupConfig(-3, Locale("by")), groupConfigRepository.get(-3))
    }
}
