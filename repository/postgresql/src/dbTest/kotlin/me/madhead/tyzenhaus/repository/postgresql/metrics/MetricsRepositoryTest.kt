package me.madhead.tyzenhaus.repository.postgresql.metrics

import me.madhead.tyzenhaus.repository.postgresql.AbstractRepositoryTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("This one is flaky: it depends on the order of execution of other tests")
class MetricsRepositoryTest : AbstractRepositoryTest() {
    private lateinit var metricsRepository: MetricsRepository

    @BeforeAll
    fun setUp() {
        metricsRepository = MetricsRepository(dataSource)
    }

    @Test
    fun totalNumberOfChats() {
        assertEquals(1, metricsRepository.totalNumberOfChats())
    }

    @Test
    fun numberOfGroupsWithTransactions() {
        assertEquals(1, metricsRepository.numberOfGroupsWithTransactions())
    }

    @Test
    fun numberOfTransactions() {
        assertEquals(1, metricsRepository.numberOfTransactions())
    }

    @Test
    fun averageGroupSize() {
        assertEquals(1.0, metricsRepository.averageGroupSize())
    }
}
