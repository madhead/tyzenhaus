package me.madhead.tyzenhaus.repository.postgresql.balance

import javax.sql.DataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager

/**
 * PostgreSQL repository for [balances][Balance].
 */
class BalanceRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.BalanceRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(BalanceRepository::class.java)!!
        private val json = Json { encodeDefaults = true }
    }

    override fun get(id: Long): Balance? {
        logger.debug("get {}", id)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("SELECT * FROM balance WHERE group_id = ?;")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        return@get resultSet.toBalance(json)
                    }
                }
        }
    }

    @Suppress("DuplicatedCode")
    override fun save(entity: Balance) {
        logger.debug("save {}", entity)

        dataSource
            .connection
            .use { connection ->
                connection
                    .prepareStatement("""
                                INSERT INTO balance ("group_id", "version", "balance")
                                VALUES (?, ?, ?::jsonb)
                                ON CONFLICT ("group_id", "version")
                                    DO UPDATE SET "version" = EXCLUDED."version" + 1,
                                                  "balance" = EXCLUDED."balance";
                            """.trimIndent())
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.groupId)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, entity.version)
                        preparedStatement.setString(@Suppress("MagicNumber") 3, json.encodeToString(entity))
                        preparedStatement.executeUpdate()
                    }
            }
    }
}
