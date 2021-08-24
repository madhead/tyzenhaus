package me.madhead.tyzenhaus.repository.postgresql.balance

import me.madhead.tyzenhaus.entity.balance.Balance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class BalanceRepositoryTest {
    lateinit var balanceRepository: BalanceRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        balanceRepository = BalanceRepository(
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
            balance,
            balanceRepository.get(1)
        )
    }

    @Test
    fun getNonExisting() {
        Assertions.assertNull(balanceRepository.get(0))
    }

    @Test
    fun save() {
        balanceRepository.save(
            balance.copy(groupId = -1, version = 1)
        )

        Assertions.assertEquals(
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

        Assertions.assertEquals(
            updated.copy(version = 2),
            balanceRepository.get(-1)
        )
    }

    val balance = Balance(
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
