package me.madhead.tyzenhaus.repository.postgresql.transaction

import me.madhead.tyzenhaus.entity.transaction.Transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class TransactionRepositoryTest {
    lateinit var transactionRepository: TransactionRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        transactionRepository = TransactionRepository(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
                user = databaseUri.userInfo.split(":")[0]
                password = databaseUri.userInfo.split(":")[1]
            }
        )
    }

    @Test
    fun get() {
        Assertions.assertEquals(
            Transaction(1, 1, 1, setOf(1, 2, 3), "42.990000".toBigDecimal(), "USD", "Lunch", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(1)
        )
    }

    @Test
    fun getNonExisting() {
        Assertions.assertNull(transactionRepository.get(0))
    }

    @Test
    fun save() {
        transactionRepository.save(
            Transaction(-1, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        Assertions.assertEquals(
            Transaction(-1, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-1)
        )
    }

    @Test
    fun update() {
        transactionRepository.save(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        Assertions.assertEquals(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "10000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-2)
        )

        transactionRepository.save(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "20000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000))
        )

        Assertions.assertEquals(
            Transaction(-2, 2, 3, setOf(3, 2, 1), "20000.000000".toBigDecimal(), "€", "Lux hotel", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(-2)
        )
    }
}
