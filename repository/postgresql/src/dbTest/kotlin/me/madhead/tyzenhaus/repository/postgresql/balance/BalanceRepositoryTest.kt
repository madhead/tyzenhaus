package me.madhead.tyzenhaus.repository.postgresql.balance

import kotlinx.coroutines.test.runTest
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.repository.postgresql.AbstractRepositoryTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class BalanceRepositoryTest : AbstractRepositoryTest() {
    private lateinit var balanceRepository: BalanceRepository

    @BeforeAll
    fun setUp() {
        balanceRepository = BalanceRepository(dataSource)
    }

    @Test
    fun get() = runTest {
        assertEquals(
            balance,
            balanceRepository.get(1)
        )
    }

    @Test
    fun getNonExisting() = runTest {
        assertNull(balanceRepository.get(0))
    }

    @Test
    fun save() = runTest {
        balanceRepository.save(
            balance.copy(groupId = -1, version = 1)
        )

        assertEquals(
            balance.copy(groupId = -1, version = 1),
            balanceRepository.get(-1)
        )

        val updated = balance.copy(
            groupId = -1,
            version = 1,
            balance = mapOf(
                "USD" to mapOf(
                    1L to "-42.99".toBigDecimal(),
                    2L to "42.99".toBigDecimal()
                ),
                "EUR" to mapOf(
                    1L to "42.99".toBigDecimal(),
                    2L to "-42.99".toBigDecimal()
                ),
                "PLN" to mapOf(
                    1L to "10".toBigDecimal(),
                    2L to "-10".toBigDecimal()
                ),
            )
        )

        balanceRepository.save(
            updated
        )

        assertEquals(
            updated.copy(version = 2),
            balanceRepository.get(-1)
        )
    }

    @Test
    fun saveOptimisticLockConflict() = runTest {
        // A brand-new balance (version 0) is inserted at version 1.
        balanceRepository.save(balance.copy(groupId = -2, version = 0))

        // Saving against a stale version must not overwrite the row.
        val thrown = runCatching {
            balanceRepository.save(balance.copy(groupId = -2, version = 42))
        }.exceptionOrNull()

        assertInstanceOf(ConcurrentModificationException::class.java, thrown)
        assertEquals(balance.copy(groupId = -2, version = 1), balanceRepository.get(-2))
    }

    private val balance = Balance(
        1,
        2,
        mapOf(
            "USD" to mapOf(
                1L to "-42.99".toBigDecimal(),
                2L to "42.99".toBigDecimal()
            ),
            "EUR" to mapOf(
                1L to "42.99".toBigDecimal(),
                2L to "-42.99".toBigDecimal()
            ),
        )
    )
}
