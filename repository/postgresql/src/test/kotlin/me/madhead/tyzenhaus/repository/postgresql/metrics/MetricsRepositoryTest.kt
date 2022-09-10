package me.madhead.tyzenhaus.repository.postgresql.metrics

import java.net.URI
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("db")
@Disabled("This one is flaky: it depends on the order of execution of other tests")
class MetricsRepositoryTest {
    private lateinit var metricsRepository: MetricsRepository

    @BeforeAll
    fun setUp() {
        val databaseUri = URI(System.getenv("DATABASE_URL")!!)

        metricsRepository = MetricsRepository(
            PGSimpleDataSource().apply {
                setUrl("jdbc:postgresql://${databaseUri.host}:${databaseUri.port}${databaseUri.path}")
                user = databaseUri.userInfo.split(":")[0]
                password = databaseUri.userInfo.split(":")[1]
            }
        )
    }

    @Test
    fun totalNumberOfChats() {
        Assertions.assertEquals(1, metricsRepository.totalNumberOfChats())
    }

    @Test
    fun numberOfGroupsWithTransactions() {
        Assertions.assertEquals(1, metricsRepository.numberOfGroupsWithTransactions())
    }

    @Test
    fun numberOfTransactions() {
        Assertions.assertEquals(1, metricsRepository.numberOfTransactions())
    }

    @Test
    fun averageGroupSize() {
        Assertions.assertEquals(1.0, metricsRepository.averageGroupSize())
    }
}
