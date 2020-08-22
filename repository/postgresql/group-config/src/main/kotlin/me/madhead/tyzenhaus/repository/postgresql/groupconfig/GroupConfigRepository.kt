package me.madhead.tyzenhaus.repository.postgresql.groupconfig

import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import me.madhead.tyzenhaus.repository.postgres.PostgreSqlRepository
import org.apache.logging.log4j.LogManager
import java.sql.Types
import javax.sql.DataSource

class GroupConfigRepository(dataSource: DataSource) : PostgreSqlRepository<Long, GroupConfig>(dataSource) {
    companion object {
        val logger = LogManager.getLogger(GroupConfigRepository::class.java)!!
    }

    fun save(groupConfig: GroupConfig) {
        logger.debug("save {}", groupConfig)

        dataSource.connection.use { connection ->
            connection
                    .prepareStatement("""
                        INSERT INTO "group_config" ("id", "language")
                        VALUES (?, ?)
                        ON CONFLICT ("id")
                            DO UPDATE SET "language" = EXCLUDED."language";
                    """.trimIndent())
                    .use { preparedStatement ->
                        preparedStatement.setLong(1, groupConfig.id)
                        groupConfig.language?.let {
                            preparedStatement.setString(2, it.language)
                        } ?: run {
                            preparedStatement.setNull(2, Types.VARCHAR)
                        }
                        preparedStatement.executeUpdate()
                    }
        }
    }

    override fun get(id: Long): GroupConfig? {
        logger.debug("get {}", id)

        dataSource.connection.use { connection ->
            connection
                    .prepareStatement("SELECT * FROM group_config WHERE id = ?;")
                    .use { preparedStatement ->
                        preparedStatement.setLong(1, id)
                        preparedStatement.executeQuery().use { resultSet ->
                            return@get resultSet.toGroupConfig()
                        }
                    }
        }
    }
}
