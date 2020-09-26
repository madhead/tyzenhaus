package me.madhead.tyzenhaus.repository.postgresql.dialog.state

import me.madhead.tyzenhaus.entity.dialog.state.ChangingLanguage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class DialogStateRepositoryTest {
    lateinit var dialogStateRepository: DialogStateRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        dialogStateRepository = DialogStateRepository(
                PGSimpleDataSource().apply {
                    setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
                    user = databaseUri.userInfo.split(":")[0]
                    password = databaseUri.userInfo.split(":")[1]
                }
        )
    }

    @Test
    fun get() {
        Assertions.assertEquals(ChangingLanguage(1, 1), dialogStateRepository.get(1, 1))
    }

    @Test
    fun getNonExisting() {
        Assertions.assertNull(dialogStateRepository.get(0, 0))
    }

    @Test
    fun saveChangingLanguage() {
        dialogStateRepository.save(ChangingLanguage(-1, -1))

        Assertions.assertEquals(ChangingLanguage(-1, -1), dialogStateRepository.get(-1, -1))
    }

    @Test
    fun update() {
        dialogStateRepository.save(ChangingLanguage(-2, -2))

        Assertions.assertEquals(ChangingLanguage(-2, -2), dialogStateRepository.get(-2, -2))
    }

    @Test
    fun delete() {
        dialogStateRepository.save(ChangingLanguage(-3, -3))

        Assertions.assertEquals(ChangingLanguage(-3, -3), dialogStateRepository.get(-3, -3))

        dialogStateRepository.delete(-3, -3)

        Assertions.assertNull(dialogStateRepository.get(-3, -3))
    }
}
