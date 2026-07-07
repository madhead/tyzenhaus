package me.madhead.tyzenhaus.repository.postgresql

import javax.sql.DataSource
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext
import me.madhead.tyzenhaus.repository.TransactionManager

/**
 * [TransactionManager] backed by a JDBC [DataSource].
 */
class PostgreSqlTransactionManager(
    private val dataSource: DataSource,
) : TransactionManager {
    override suspend fun <T> transaction(block: suspend () -> T): T {
        if (TransactionContext.current.get() != null) return block()

        return dataSource.connection.use { connection ->
            connection.autoCommit = false

            try {
                withContext(TransactionContext.current.asContextElement(connection)) {
                    block()
                }.also {
                    connection.commit()
                }
            } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
                connection.rollback()
                throw e
            }
        }
    }
}
