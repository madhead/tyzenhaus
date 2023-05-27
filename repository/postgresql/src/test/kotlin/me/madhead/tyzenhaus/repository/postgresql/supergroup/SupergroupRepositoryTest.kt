package me.madhead.tyzenhaus.repository.postgresql.supergroup

import java.net.URI
import java.time.Instant
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.repository.postgresql.balance.BalanceRepository
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.transaction.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
class SupergroupRepositoryTest {
    private lateinit var supergroupRepository: SupergroupRepository
    private lateinit var groupConfigRepository: GroupConfigRepository
    private lateinit var dialogStateRepository: DialogStateRepository
    private lateinit var balanceRepository: BalanceRepository
    private lateinit var transactionRepository: TransactionRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)
        val dataSource = PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
            user = databaseUri.userInfo.split(":")[0]
            password = databaseUri.userInfo.split(":")[1]
        }

        supergroupRepository = SupergroupRepository(dataSource)
        groupConfigRepository = GroupConfigRepository(dataSource)
        dialogStateRepository = DialogStateRepository(dataSource)
        balanceRepository = BalanceRepository(dataSource)
        transactionRepository = TransactionRepository(dataSource)
    }

    @Test
    fun update() {
        supergroupRepository.update(6, 7)

        assertNull(groupConfigRepository.get(6))
        assertEquals(GroupConfig(7, 1, null, null, setOf(1, 2, 3)), groupConfigRepository.get(7))

        assertNull(dialogStateRepository.get(6, 1))
        assertNull(dialogStateRepository.get(6, 2))
        // It's ok to have the old group id here. Instead of the group id from the state, always use update.groupId
        assertEquals(WaitingForAmount(6, 1, 42), dialogStateRepository.get(7, 1))
        assertEquals(WaitingForAmount(6, 2, 43), dialogStateRepository.get(7, 2))

        assertNull(balanceRepository.get(6))
        assertEquals(Balance(7), balanceRepository.get(7))

        assertEquals(
            Transaction(2, 7, 1, setOf(1, 2, 3), "42.990000".toBigDecimal(), "USD", "Breakfast", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(2)
        )
        assertEquals(
            Transaction(3, 7, 2, setOf(1, 2, 3), "42.990000".toBigDecimal(), "USD", "Lunch", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(3)
        )
        assertEquals(
            Transaction(4, 7, 3, setOf(1, 2, 3), "42.990000".toBigDecimal(), "USD", "Dinner", Instant.ofEpochMilli(808174800000)),
            transactionRepository.get(4)
        )
    }
}
