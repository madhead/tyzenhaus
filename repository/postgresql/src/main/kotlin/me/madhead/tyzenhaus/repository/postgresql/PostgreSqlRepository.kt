package me.madhead.tyzenhaus.repository.postgresql

import java.sql.Connection
import javax.sql.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

/**
 * Base class for PostgreSQL repositories.
 */
abstract class PostgreSqlRepository(
    protected val dataSource: DataSource
) {
    /**
     * Runs the blocking [block] with a database [Connection], off-loading it to [Dispatchers.IO].
     */
    protected suspend fun <T> withConnection(block: (Connection) -> T): T {
        currentCoroutineContext()[TransactionConnection]?.let { return block(it.connection) }

        return withContext(Dispatchers.IO) {
            dataSource.connection.use(block)
        }
    }
}
