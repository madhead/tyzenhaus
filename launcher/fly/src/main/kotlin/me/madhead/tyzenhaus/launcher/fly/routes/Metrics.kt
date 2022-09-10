package me.madhead.tyzenhaus.launcher.fly.routes

import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.ktor.ext.inject

/**
 * Routes for [metrics](https://ktor.io/docs/micrometer-metrics.html).
 */
fun Route.metrics() {
    val registry by inject<PrometheusMeterRegistry>()
    val config by inject<ApplicationConfig>()
    val managementPort = config.property("deployment.managementPort").getString().toInt()

    get("metrics") {
        if (call.request.local.port == managementPort) {
            this.call.respond(registry.scrape())
        }
    }
}
