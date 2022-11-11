package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.micrometer.core.instrument.MultiGauge
import io.micrometer.core.instrument.Tags
import io.micrometer.prometheus.PrometheusMeterRegistry
import me.madhead.tyzenhaus.repository.MetricsRepository
import org.koin.ktor.ext.inject

/**
 * Routes for [metrics](https://ktor.io/docs/micrometer-metrics.html).
 */
fun Route.metrics() {
    val registry by inject<PrometheusMeterRegistry>()
    val config by inject<ApplicationConfig>()
    val managementPort = config.property("deployment.managementPort").getString().toInt()
    val metricsRepository by inject<MetricsRepository>()

    get("metrics") {
        if (call.request.local.port == managementPort) {
            val languages = MultiGauge
                .builder("tyzenhaus.languages")
                .description("Language statistics")
                .register(registry);

            languages.register(
                metricsRepository.languages().map { (language, count) ->
                    println("$language: $count")
                    MultiGauge.Row.of(Tags.of("language", language), count)
                },
                true,
            )

            this.call.respond(registry.scrape())
        }
    }
}
