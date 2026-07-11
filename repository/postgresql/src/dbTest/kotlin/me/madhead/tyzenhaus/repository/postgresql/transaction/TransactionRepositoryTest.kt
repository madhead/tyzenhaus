package me.madhead.tyzenhaus.repository.postgresql.transaction

import java.time.Instant
import kotlinx.coroutines.test.runTest
import me.madhead.tyzenhaus.entity.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import me.madhead.tyzenhaus.repository.postgresql.AbstractRepositoryTest

class TransactionRepositoryTest : AbstractRepositoryTest() {
    private lateinit var transactionRepository: TransactionRepository

    @BeforeAll
    fun setUp() {
        transactionRepository = TransactionRepository(dataSource)
    }

    @Test
    fun get() = runTest {
        assertEquals(
            Transaction(1, 1, 1, setOf(1, 2, 3), "42.990000".toBigDecimal(), "USD", "Lunch", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(1)
        )
    }

    @Test
    fun getNonExisting() = runTest {
        assertNull(transactionRepository.get(0))
    }

    @Test
    fun save() = runTest {
        transactionRepository.save(
            Transaction(-1, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        assertEquals(
            Transaction(-1, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-1)
        )
    }

    @Test
    fun update() = runTest {
        transactionRepository.save(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        assertEquals(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-2)
        )

        transactionRepository.save(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "20000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        assertEquals(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "20000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-2)
        )
    }

    @Test
    fun groupCurrencies() = runTest {
        assertEquals(
            listOf("USD"),
            transactionRepository.groupCurrencies(1)
        )
    }

    @Test
    fun search() = runTest {
        assertTrue(
            transactionRepository.search(1).isNotEmpty()
        )
        assertTrue(
            transactionRepository.search(8).size == 3
        )
    }
}
