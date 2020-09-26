package me.madhead.tyzenhaus.repository.postgresql.dialog.state

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager
import javax.sql.DataSource

/**
 * PostgreSQL repository for [dialog states][DialogState].
 */
class DialogStateRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.DialogStateRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(DialogStateRepository::class.java)!!
        private val json = Json { serializersModule = DialogState.serializers }
    }

    override fun get(groupId: Long, userId: Long): DialogState? {
        logger.debug("get {}×{}", groupId, userId)

        dataSource.connection.use { connection ->
            connection
                    .prepareStatement("SELECT * FROM dialog_state WHERE group_id = ? AND user_id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, groupId)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, userId)
                        preparedStatement.executeQuery().use { resultSet ->
                            return@get resultSet.toDialogState(json)
                        }
                    }
        }
    }

    override fun save(entity: DialogState) {
        logger.debug("save {}", entity)

        dataSource
                .connection
                .use { connection ->
                    connection
                            .prepareStatement("""
                                INSERT INTO dialog_state ("group_id", "user_id", "state")
                                VALUES (?, ?, ?::jsonb)
                                ON CONFLICT ("group_id", "user_id")
                                    DO UPDATE SET "state" = EXCLUDED."state";
                            """.trimIndent())
                            .use { preparedStatement ->
                                preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.groupId)
                                preparedStatement.setLong(@Suppress("MagicNumber") 2, entity.userId)
                                preparedStatement.setString(@Suppress("MagicNumber") 3, json.encodeToString(entity))
                                preparedStatement.executeUpdate()
                            }
                }

    }

    override fun delete(groupId: Long, userId: Long) {
        logger.debug("get {}×{}", groupId, userId)

        dataSource
                .connection
                .use { connection ->
                    connection
                            .prepareStatement("DELETE FROM dialog_state WHERE group_id = ? AND user_id = ?;")
                            .use { preparedStatement ->
                                preparedStatement.setLong(@Suppress("MagicNumber") 1, groupId)
                                preparedStatement.setLong(@Suppress("MagicNumber") 2, userId)
                                preparedStatement.executeUpdate()
                            }
                }
    }
}
