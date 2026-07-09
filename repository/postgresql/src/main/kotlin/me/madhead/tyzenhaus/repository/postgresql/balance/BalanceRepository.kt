package me.madhead.tyzenhaus.repository.postgresql.balance

import javax.sql.DataSource
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

    override suspend fun get(id: Long): Balance? {
        logger.debug("get {}", id)

        return withConnection { connection ->
            connection
                .prepareStatement("SELECT * FROM balance WHERE group_id = ?;")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        resultSet.toBalance(json)
                    }
                }
        }
    }

    override suspend fun save(entity: Balance) {
        logger.debug("save {}", entity)

        withConnection { connection ->
            connection
                .prepareStatement("""
                            INSERT INTO balance ("group_id", "version", "balance")
                            VALUES (?, 1, ?::jsonb)
                            ON CONFLICT ("group_id")
                                DO UPDATE SET "version" = balance."version" + 1,
                                              "balance" = EXCLUDED."balance"
                                WHERE balance."version" = ?;
                        """.trimIndent())
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.groupId)
                    preparedStatement.setString(@Suppress("MagicNumber") 2, json.encodeToString(entity))
                    preparedStatement.setLong(@Suppress("MagicNumber") 3, entity.version)

                    if (preparedStatement.executeUpdate() == 0) {
                        throw ConcurrentModificationException()
                    }
                }
        }
    }
}
