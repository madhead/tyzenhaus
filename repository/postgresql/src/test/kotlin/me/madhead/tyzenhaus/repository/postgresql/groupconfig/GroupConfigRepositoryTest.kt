package me.madhead.tyzenhaus.repository.postgresql.groupconfig

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import org.junit.jupiter.api.*
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import java.util.*

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
    fun save() {
        groupConfigRepository.save(GroupConfig(2020, Locale("by")))

        val groupConfig = groupConfigRepository.get(2020)

        Assertions.assertNotNull(groupConfig)
        Assertions.assertEquals(2020, groupConfig?.id)
        Assertions.assertEquals("by", groupConfig?.language?.language)
    }

    @Test
    fun saveNullLanguage() {
        groupConfigRepository.save(GroupConfig(2020))

        val groupConfig = groupConfigRepository.get(2020)

        Assertions.assertNotNull(groupConfig)
        Assertions.assertEquals(2020, groupConfig?.id)
        Assertions.assertNull(groupConfig?.language)
    }

    @Test
    fun update() {
        groupConfigRepository.save(GroupConfig(2020, Locale("en")))
        groupConfigRepository.save(GroupConfig(2020, Locale("by")))

        val groupConfig = groupConfigRepository.get(2020)

        Assertions.assertNotNull(groupConfig)
        Assertions.assertEquals(2020, groupConfig?.id)
        Assertions.assertEquals("by", groupConfig?.language?.language)
    }

    @Test
    fun getNonExisting() {
        groupConfigRepository.save(GroupConfig(2020, Locale("by")))

        val groupConfig = groupConfigRepository.get(0)

        Assertions.assertNull(groupConfig)
    }
}
