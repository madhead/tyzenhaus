package me.madhead.tyzenhaus.repository.postgresql

import kotlinx.coroutines.test.runTest
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import me.madhead.tyzenhaus.repository.postgresql.balance.BalanceRepository
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PostgreSqlTransactionManagerTest : AbstractRepositoryTest() {
    private lateinit var transactionManager: PostgreSqlTransactionManager
    private lateinit var balanceRepository: BalanceRepository
    private lateinit var dialogStateRepository: DialogStateRepository

    @BeforeAll
    fun setUp() {
        transactionManager = PostgreSqlTransactionManager(dataSource)
        balanceRepository = BalanceRepository(dataSource)
        dialogStateRepository = DialogStateRepository(dataSource)
    }

    @Test
    fun commitsAllWritesTogether() = runTest {
        transactionManager.transaction {
            balanceRepository.save(Balance(-10))
            dialogStateRepository.save(WaitingForAmount(-10, -10, 42))
        }

        assertEquals(Balance(-10, version = 1), balanceRepository.get(-10))
        assertEquals(WaitingForAmount(-10, -10, 42), dialogStateRepository.get(-10, -10))
    }

    @Test
    fun rollsBackAllWritesOnFailure() = runTest {
        // The writes below land on the same connection; when the block throws, none of them must survive.
        val thrown = runCatching {
            transactionManager.transaction {
                balanceRepository.save(Balance(-20))
                dialogStateRepository.save(WaitingForAmount(-20, -20, 42))
                error("boom")
            }
        }.exceptionOrNull()

        assertInstanceOf(IllegalStateException::class.java, thrown)
        assertNull(balanceRepository.get(-20))
        assertNull(dialogStateRepository.get(-20, -20))
    }
}
