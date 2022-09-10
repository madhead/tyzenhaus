package me.madhead.tyzenhaus.repository.postgresql.metrics

import java.sql.ResultSet
import javax.sql.DataSource
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository

/**
 * PostgreSQL repository for metrics.
 */
class MetricsRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.MetricsRepository, PostgreSqlRepository(dataSource) {
    override fun totalNumberOfChats(): Int =
        execute("SELECT COUNT(*) FROM group_config;") {
            if (it.next()) {
                it.getInt(1)
            } else {
                0
            }
        }

    override fun numberOfGroupsWithTransactions(): Int =
        execute("SELECT COUNT(DISTINCT (group_id)) FROM transaction;") {
            if (it.next()) {
                it.getInt(1)
            } else {
                0
            }
        }

    override fun numberOfTransactions(): Int =
        execute("SELECT COUNT(*) FROM transaction;") {
            if (it.next()) {
                it.getInt(1)
            } else {
                0
            }
        }

    override fun averageGroupSize(): Double =
        execute("SELECT AVG(ARRAY_LENGTH(members, 1)) FROM group_config;") {
            if (it.next()) {
                it.getDouble(1)
            } else {
                0.0
            }
        }

    private fun <T> execute(sql: String, action: (ResultSet) -> T): T {
        return dataSource.connection.use { connection ->
            connection
                .prepareStatement(sql)
                .use { statement ->
                    statement
                        .executeQuery()
                        .use { resultSet ->
                            action(resultSet)
                        }
                }
        }
    }
}
