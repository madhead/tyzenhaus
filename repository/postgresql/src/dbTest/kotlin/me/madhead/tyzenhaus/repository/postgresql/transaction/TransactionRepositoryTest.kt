package me.madhead.tyzenhaus.repository.postgresql.transaction

import java.time.Instant
import kotlinx.coroutines.test.runTest
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
            transactionRepository.search(1, TransactionsSearchParams()).transactions.isNotEmpty()
        )
        assertEquals(
            3,
            transactionRepository.search(8, TransactionsSearchParams()).transactions.size
        )
    }

    @Test
    fun searchReturnsAllNewestFirst() = runTest {
        val page = transactionRepository.search(9, TransactionsSearchParams())

        assertEquals(listOf(105L, 104L, 103L, 102L, 101L), page.transactions.map { it.id })
        assertNull(page.nextCursor)
    }

    @Test
    fun searchByTitleIsCaseInsensitiveSubstring() = runTest {
        assertEquals(
            listOf(103L),
            transactionRepository.search(9, TransactionsSearchParams(title = "din")).transactions.map { it.id }
        )
        assertEquals(
            listOf(103L),
            transactionRepository.search(9, TransactionsSearchParams(title = "DIN")).transactions.map { it.id }
        )
    }

    @Test
    fun searchByParticipantMatchesPayerOrRecipient() = runTest {
        // Member 1 is payer of 101/104, recipient of 101/103/105.
        assertEquals(
            listOf(105L, 104L, 103L, 101L),
            transactionRepository.search(9, TransactionsSearchParams(participants = setOf(1))).transactions.map { it.id }
        )
    }

    @Test
    fun searchByParticipantIsAnyOf() = runTest {
        // Member 3 appears (as payer or recipient) in every row except 101.
        assertEquals(
            listOf(105L, 104L, 103L, 102L),
            transactionRepository.search(9, TransactionsSearchParams(participants = setOf(3))).transactions.map { it.id }
        )
    }

    @Test
    fun searchByAmountRange() = runTest {
        assertEquals(
            listOf(104L, 103L),
            transactionRepository.search(
                9,
                TransactionsSearchParams(amountFrom = "25".toBigDecimal(), amountTo = "45".toBigDecimal())
            ).transactions.map { it.id }
        )
        assertEquals(
            listOf(105L, 104L),
            transactionRepository.search(9, TransactionsSearchParams(amountFrom = "40".toBigDecimal())).transactions.map { it.id }
        )
    }

    @Test
    fun searchByCurrency() = runTest {
        assertEquals(
            listOf(104L, 102L),
            transactionRepository.search(9, TransactionsSearchParams(currencies = setOf("EUR"))).transactions.map { it.id }
        )
        assertEquals(
            listOf(105L, 104L, 103L, 102L, 101L),
            transactionRepository.search(9, TransactionsSearchParams(currencies = setOf("USD", "EUR"))).transactions.map { it.id }
        )
    }

    @Test
    fun searchByDateRange() = runTest {
        assertEquals(
            listOf(104L, 103L, 102L),
            transactionRepository.search(
                9,
                TransactionsSearchParams(
                    dateFrom = Instant.parse("2024-01-02T00:00:00Z"),
                    dateTo = Instant.parse("2024-01-04T23:59:59Z"),
                )
            ).transactions.map { it.id }
        )
    }

    @Test
    fun searchCombinesFilters() = runTest {
        // USD rows involving member 3: 103 (payer) and 105 (recipient); 101 is USD but does not involve 3.
        assertEquals(
            listOf(105L, 103L),
            transactionRepository.search(
                9,
                TransactionsSearchParams(currencies = setOf("USD"), participants = setOf(3))
            ).transactions.map { it.id }
        )
    }

    @Test
    fun searchReturnsEmptyWhenNothingMatches() = runTest {
        val page = transactionRepository.search(9, TransactionsSearchParams(title = "nonexistent"))

        assertTrue(page.transactions.isEmpty())
        assertNull(page.nextCursor)
    }

    @Test
    fun searchPaginatesWithKeysetCursor() = runTest {
        val first = transactionRepository.search(9, TransactionsSearchParams(limit = 2))
        assertEquals(listOf(105L, 104L), first.transactions.map { it.id })
        assertNotNull(first.nextCursor)

        val second = transactionRepository.search(9, TransactionsSearchParams(limit = 2, cursor = first.nextCursor))
        assertEquals(listOf(103L, 102L), second.transactions.map { it.id })
        assertNotNull(second.nextCursor)

        val third = transactionRepository.search(9, TransactionsSearchParams(limit = 2, cursor = second.nextCursor))
        assertEquals(listOf(101L), third.transactions.map { it.id })
        assertNull(third.nextCursor)
    }
}
