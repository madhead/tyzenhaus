package me.madhead.tyzenhaus.repository.postgresql.group.config

import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager
import java.sql.Timestamp
import java.sql.Types
import javax.sql.DataSource

/**
 * PostgreSQL repository for [group configs][GroupConfig].
 */
class GroupConfigRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.GroupConfigRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(GroupConfigRepository::class.java)!!
    }

    override fun get(id: Long): GroupConfig? {
        logger.debug("get {}", id)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("SELECT * FROM group_config WHERE id = ?;")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        return@get resultSet.toGroupConfig()
                    }
                }
        }
    }

    @Suppress("ComplexMethod")
    override fun save(entity: GroupConfig) {
        logger.debug("save {}", entity)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("""
                        INSERT INTO "group_config" ("id", "invited_by", "invited_at", "language", "members")
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT ("id")
                            DO UPDATE SET "invited_by" = EXCLUDED."invited_by",
                                          "invited_at" = EXCLUDED."invited_at",
                                          "language" = EXCLUDED."language",
                                          "members" = EXCLUDED."members";
                    """.trimIndent())
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.id)
                    entity.invitedBy?.let {
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, it)
                    } ?: run {
                        preparedStatement.setNull(@Suppress("MagicNumber") 2, Types.BIGINT)
                    }
                    entity.invitedAt?.let {
                        preparedStatement.setTimestamp(@Suppress("MagicNumber") 3, Timestamp.from(it))
                    } ?: run {
                        preparedStatement.setNull(@Suppress("MagicNumber") 3, Types.TIMESTAMP)
                    }
                    entity.language?.let {
                        preparedStatement.setString(@Suppress("MagicNumber") 4, it.language)
                    } ?: run {
                        preparedStatement.setNull(@Suppress("MagicNumber") 4, Types.VARCHAR)
                    }
                    entity.members.takeIf { it.isNotEmpty() }?.let {
                        preparedStatement.setArray(
                            @Suppress("MagicNumber") 5,
                            connection.createArrayOf("bigint", it.toTypedArray())
                        )
                    } ?: run {
                        preparedStatement.setNull(@Suppress("MagicNumber") 5, Types.ARRAY)
                    }
                    preparedStatement.executeUpdate()
                }
        }
    }
}
