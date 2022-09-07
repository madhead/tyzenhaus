package me.madhead.tyzenhaus.launcher.fly.stats

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import java.sql.Connection
import java.time.Instant
import javax.sql.DataSource
import me.madhead.tyzenhaus.launcher.fly.stats.koin.dbModule
import me.madhead.tyzenhaus.launcher.fly.stats.koin.influxModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

/**
 * Gathers some stats.
 */
suspend fun main() {
    startKoin {
        modules(
            dbModule,
            influxModule,
        )
    }

    TyzenhausStats().run()
}

internal class TyzenhausStats : KoinComponent {
    private val dataSource by inject<DataSource>()
    private val influx by inject<InfluxDBClientKotlin>()

    suspend fun run() {
        val stats = Stats()

        dataSource.connection?.use { connection ->
            totalNumberOfChats(connection, stats)
            numberOfGroupsWithTransactions(connection, stats)
            numberOfTransactions(connection, stats)
            averageGroupSize(connection, stats)
        }

        influx.getWriteKotlinApi().writeMeasurement(
            measurement = stats,
            precision = WritePrecision.S,
        )

        println(stats)
    }

    @Suppress("DataClassShouldBeImmutable")
    @Measurement(name = "KPI")
    data class Stats(
        @Column var totalNumberOfChats: Int = 0,
        @Column var numberOfGroupsWithTransactions: Int = 0,
        @Column var numberOfTransactions: Int = 0,
        @Column var averageGroupSize: Double = 0.0,
        @Column(timestamp = true) val time: Instant = Instant.now()
    )

    private fun totalNumberOfChats(connection: Connection, stats: Stats) {
        connection
            .prepareStatement("""
                SELECT COUNT(*)
                FROM group_config;
            """.trimIndent())
            ?.use { statement ->
                statement
                    .executeQuery()
                    ?.use { resultSet ->
                        stats.totalNumberOfChats = if (resultSet.next()) {
                            resultSet.getInt(1)
                        } else {
                            0
                        }
                    }
            }
    }

    private fun numberOfGroupsWithTransactions(connection: Connection, stats: Stats) {
        connection
            .prepareStatement("""
                SELECT COUNT(DISTINCT (group_id))
                FROM transaction;
            """.trimIndent())
            ?.use { statement ->
                statement
                    .executeQuery()
                    ?.use { resultSet ->
                        stats.numberOfGroupsWithTransactions = if (resultSet.next()) {
                            resultSet.getInt(1)
                        } else {
                            0
                        }
                    }
            }
    }

    private fun numberOfTransactions(connection: Connection, stats: Stats) {
        connection
            .prepareStatement("""
                SELECT COUNT(*)
                FROM transaction;
            """.trimIndent())
            ?.use { statement ->
                statement
                    .executeQuery()
                    ?.use { resultSet ->
                        stats.numberOfTransactions = if (resultSet.next()) {
                            resultSet.getInt(1)
                        } else {
                            0
                        }
                    }
            }
    }

    private fun averageGroupSize(connection: Connection, stats: Stats) {
        connection
            .prepareStatement("""
                SELECT AVG(ARRAY_LENGTH(members, 1))
                FROM group_config;
            """.trimIndent())
            ?.use { statement ->
                statement
                    .executeQuery()
                    ?.use { resultSet ->
                        stats.averageGroupSize = if (resultSet.next()) {
                            resultSet.getDouble(1)
                        } else {
                            0.0
                        }
                    }
            }
    }
}
