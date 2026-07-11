package me.madhead.tyzenhaus.launcher.fly.koin

import io.micrometer.core.instrument.FunctionCounter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.runBlocking
import me.madhead.tyzenhaus.repository.MetricsRepository
import org.koin.dsl.binds
import org.koin.dsl.module

val metricsModule = module {
    single {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
        val metricsRepository = get<MetricsRepository>()

        FunctionCounter
            .builder("tyzenhaus.chats", Unit) {
                runBlocking { metricsRepository.totalNumberOfChats().toDouble() }
            }
            .description("Total number of chats ever started with the bot")
            .register(registry)
        FunctionCounter
            .builder("tyzenhaus.groups.with.transactions", Unit) {
                runBlocking { metricsRepository.numberOfGroupsWithTransactions().toDouble() }
            }
            .description("Total number of groups with transactions")
            .register(registry)
        FunctionCounter
            .builder("tyzenhaus.transactions", Unit) {
                runBlocking { metricsRepository.numberOfTransactions().toDouble() }
            }
            .description("Total number of transactions")
            .register(registry)
        Gauge
            .builder("tyzenhaus.average.group.size") {
                runBlocking { metricsRepository.averageGroupSize() }
            }
            .description("Average group size")
            .register(registry)

        registry
    }.binds(arrayOf(MeterRegistry::class, PrometheusMeterRegistry::class))
}
