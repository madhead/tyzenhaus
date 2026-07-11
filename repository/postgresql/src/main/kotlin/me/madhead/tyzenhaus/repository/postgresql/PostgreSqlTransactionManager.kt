package me.madhead.tyzenhaus.repository.postgresql

import javax.sql.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import me.madhead.tyzenhaus.repository.TransactionManager

/**
 * [TransactionManager] backed by a JDBC [DataSource].
 */
class PostgreSqlTransactionManager(
    private val dataSource: DataSource,
) : TransactionManager {
    override suspend fun <T> transaction(block: suspend () -> T): T {
        if (currentCoroutineContext()[TransactionConnection] != null) return block()

        return withContext(Dispatchers.IO) {
            dataSource.connection.use { connection ->
                connection.autoCommit = false

                try {
                    withContext(TransactionConnection(connection)) {
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
}
