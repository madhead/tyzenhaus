package me.madhead.tyzenhaus.launcher.fly.util

import io.ktor.http.Parameters
import java.math.BigDecimal
import java.time.Instant
import me.madhead.tyzenhaus.entity.transaction.Cursor
import me.madhead.tyzenhaus.entity.transaction.DEFAULT_TRANSACTIONS_PAGE_SIZE
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ParametersExtensionsTest {
    @Test
    fun `empty params yield the defaults`() {
        val params = Parameters.Empty.toTransactionsSearchParams()

        Assertions.assertEquals(TransactionsSearchParams(), params)
    }

    @Test
    fun `a present title is kept`() {
        val params = Parameters.build { append("title", "coffee") }.toTransactionsSearchParams()

        Assertions.assertEquals("coffee", params.title)
    }

    @Test
    fun `a blank title is dropped`() {
        val params = Parameters.build { append("title", "   ") }.toTransactionsSearchParams()

        Assertions.assertNull(params.title)
    }

    @Test
    fun `multiple participants are collected, non-numeric ones are dropped`() {
        val params = Parameters.build {
            append("participant", "1")
            append("participant", "2")
            append("participant", "not-a-number")
        }.toTransactionsSearchParams()

        Assertions.assertEquals(setOf(1L, 2L), params.participants)
    }

    @Test
    fun `amount bounds are parsed`() {
        val params = Parameters.build {
            append("amountMin", "1.50")
            append("amountMax", "42")
        }.toTransactionsSearchParams()

        Assertions.assertEquals(BigDecimal("1.50"), params.amountFrom)
        Assertions.assertEquals(BigDecimal("42"), params.amountTo)
    }

    @Test
    fun `malformed amount bounds are dropped`() {
        val params = Parameters.build {
            append("amountMin", "not-a-number")
            append("amountMax", "also-not-a-number")
        }.toTransactionsSearchParams()

        Assertions.assertNull(params.amountFrom)
        Assertions.assertNull(params.amountTo)
    }

    @Test
    fun `multiple currencies are collected, blanks are dropped`() {
        val params = Parameters.build {
            append("currency", "USD")
            append("currency", "EUR")
            append("currency", "")
        }.toTransactionsSearchParams()

        Assertions.assertEquals(setOf("USD", "EUR"), params.currencies)
    }

    @Test
    fun `date bounds are parsed`() {
        val from = "2024-01-01T00:00:00Z"
        val to = "2024-12-31T23:59:59Z"
        val params = Parameters.build {
            append("dateFrom", from)
            append("dateTo", to)
        }.toTransactionsSearchParams()

        Assertions.assertEquals(Instant.parse(from), params.dateFrom)
        Assertions.assertEquals(Instant.parse(to), params.dateTo)
    }

    @Test
    fun `malformed date bounds are dropped`() {
        val params = Parameters.build {
            append("dateFrom", "not-a-date")
            append("dateTo", "also-not-a-date")
        }.toTransactionsSearchParams()

        Assertions.assertNull(params.dateFrom)
        Assertions.assertNull(params.dateTo)
    }

    @Test
    fun `a valid cursor round-trips`() {
        val cursor = Cursor(Instant.parse("2024-06-15T12:00:00Z"), 123L)
        val params = Parameters.build { append("cursor", cursor.encode()) }.toTransactionsSearchParams()

        Assertions.assertEquals(cursor, params.cursor)
    }

    @Test
    fun `a garbage cursor is dropped`() {
        val params = Parameters.build { append("cursor", "not-a-valid-cursor-token") }.toTransactionsSearchParams()

        Assertions.assertNull(params.cursor)
    }

    @Test
    fun `a valid limit is honored as-is`() {
        val params = Parameters.build { append("limit", "5") }.toTransactionsSearchParams()

        Assertions.assertEquals(5, params.limit)
    }

    @Test
    fun `a zero, negative, non-numeric or absent limit falls back to the default`() {
        listOf("0", "-1", "nope").forEach { limit ->
            Assertions.assertEquals(
                DEFAULT_TRANSACTIONS_PAGE_SIZE,
                Parameters.build { append("limit", limit) }.toTransactionsSearchParams().limit,
            )
        }
        Assertions.assertEquals(DEFAULT_TRANSACTIONS_PAGE_SIZE, Parameters.Empty.toTransactionsSearchParams().limit)
    }
}
