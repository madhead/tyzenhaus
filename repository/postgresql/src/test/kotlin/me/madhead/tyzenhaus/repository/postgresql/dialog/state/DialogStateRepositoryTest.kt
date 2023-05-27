package me.madhead.tyzenhaus.repository.postgresql.dialog.state

import java.net.URI
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class DialogStateRepositoryTest {
    private lateinit var dialogStateRepository: DialogStateRepository

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
        assertEquals(WaitingForAmount(1, 1, 42), dialogStateRepository.get(1, 1))
    }

    @Test
    fun getNonExisting() {
        assertNull(dialogStateRepository.get(0, 0))
    }

    @Test
    fun saveChangingLanguage() {
        dialogStateRepository.save(WaitingForAmount(-1, -1, 42))

        assertEquals(WaitingForAmount(-1, -1, 42), dialogStateRepository.get(-1, -1))
    }

    @Test
    fun update() {
        dialogStateRepository.save(WaitingForAmount(-2, -2, 42))

        assertEquals(WaitingForAmount(-2, -2, 42), dialogStateRepository.get(-2, -2))

        dialogStateRepository.save(WaitingForAmount(-2, -2, 43))

        assertEquals(WaitingForAmount(-2, -2, 43), dialogStateRepository.get(-2, -2))
    }

    @Test
    fun delete() {
        dialogStateRepository.save(WaitingForAmount(-3, -3, 33))

        assertEquals(WaitingForAmount(-3, -3, 33), dialogStateRepository.get(-3, -3))

        dialogStateRepository.delete(-3, -3)

        assertNull(dialogStateRepository.get(-3, -3))
    }
}
