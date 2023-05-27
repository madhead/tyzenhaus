package me.madhead.tyzenhaus.repository.postgresql.supergroup

import java.sql.Connection.TRANSACTION_SERIALIZABLE
import javax.sql.DataSource
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager

/**
 * PostgreSQL repository for supergroup updates.
 */
class SupergroupRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.SupergroupRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(SupergroupRepository::class.java)!!
    }

    override fun update(from: Long, to: Long) {
        logger.debug("update {} -> {}", from, to)

        dataSource.connection.use { connection ->
            connection.autoCommit = false
            connection.transactionIsolation = TRANSACTION_SERIALIZABLE

            try {
                connection
                    .prepareStatement("UPDATE group_config SET id = ? WHERE id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, to)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, from)
                        preparedStatement.executeUpdate()
                    }
                connection
                    .prepareStatement("UPDATE dialog_state SET group_id = ? WHERE group_id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, to)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, from)
                        preparedStatement.executeUpdate()
                    }
                connection
                    .prepareStatement("UPDATE balance SET group_id = ? WHERE group_id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, to)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, from)
                        preparedStatement.executeUpdate()
                    }
                connection
                    .prepareStatement("UPDATE transaction SET group_id = ? WHERE group_id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, to)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, from)
                        preparedStatement.executeUpdate()
                    }

                connection.commit()
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.error("Failed to update the supergroup", e)

                connection.rollback()
            }
        }
    }
}
