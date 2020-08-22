package me.madhead.tyzenhaus.repository.postgresql.groupconfig

import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.util.Locale

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupConfigRepositoryTest {
    lateinit var groupConfigRepository: GroupConfigRepository

    @BeforeAll
    fun setUp() {
        groupConfigRepository = GroupConfigRepository(
                PGSimpleDataSource().apply {
                    setUrl("jdbc:postgresql://${System.getenv("POSTGRES_HOST")!!}:${System.getenv("POSTGRES_PORT")!!}/${System.getenv("POSTGRES_DB")!!}")
                    user = System.getenv("POSTGRES_USER")!!
                    password = System.getenv("POSTGRES_PASSWORD")!!
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
