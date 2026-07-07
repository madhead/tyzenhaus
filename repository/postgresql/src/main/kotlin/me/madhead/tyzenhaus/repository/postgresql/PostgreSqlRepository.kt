package me.madhead.tyzenhaus.repository.postgresql

import java.sql.Connection
import javax.sql.DataSource

/**
 * Base class for PostgreSQL repositories.
 */
abstract class PostgreSqlRepository(
    protected val dataSource: DataSource
) {
    /**
     * Runs [block] with a database [Connection].
     */
    protected inline fun <T> withConnection(block: (Connection) -> T): T {
        val ambient = TransactionContext.current.get()

        return if (ambient != null) {
            block(ambient)
        } else {
            dataSource.connection.use(block)
        }
    }
}
