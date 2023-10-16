package me.madhead.tyzenhaus.repository.postgresql.api.token

import java.sql.Timestamp
import java.util.UUID
import javax.sql.DataSource
import me.madhead.tyzenhaus.entity.api.token.APIToken
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager

/**
 * PostgreSQL repository for [balances][Balance].
 */
class APITokenRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.APITokenRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(APITokenRepository::class.java)!!
    }

    override fun get(id: UUID): APIToken? {
        logger.debug("get {}", id)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("""SELECT * FROM "api_token" WHERE "token" = ?;""")
                .use { preparedStatement ->
                    preparedStatement.setObject(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        return@get resultSet.toAPIToken()
                    }
                }
        }
    }

    @Suppress("DuplicatedCode")
    override fun save(entity: APIToken) {
        logger.debug("save {}", entity)

        dataSource
            .connection
            .use { connection ->
                connection
                    .prepareStatement("""
                                INSERT INTO "api_token" ("token", "group_id", "scope", "valid_until")
                                VALUES (?, ?, ?, ?)
                            """.trimIndent())
                    .use { preparedStatement ->
                        preparedStatement.setObject(@Suppress("MagicNumber") 1, entity.token)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, entity.groupId)
                        preparedStatement.setString(@Suppress("MagicNumber") 3, entity.scope.name)
                        preparedStatement.setTimestamp(@Suppress("MagicNumber") 4, Timestamp.from(entity.validUntil))
                        preparedStatement.executeUpdate()
                    }
            }
    }
}
