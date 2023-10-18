package me.madhead.tyzenhaus.launcher.fly.modules

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusMeterRegistry
import me.madhead.tyzenhaus.launcher.fly.koin.configModule
import me.madhead.tyzenhaus.launcher.fly.koin.dbModule
import me.madhead.tyzenhaus.launcher.fly.koin.jsonModule
import me.madhead.tyzenhaus.launcher.fly.koin.metricsModule
import me.madhead.tyzenhaus.launcher.fly.koin.pipelineModule
import me.madhead.tyzenhaus.launcher.fly.koin.telegramModule
import me.madhead.tyzenhaus.launcher.fly.routes.metrics
import me.madhead.tyzenhaus.launcher.fly.routes.miniApp
import me.madhead.tyzenhaus.launcher.fly.routes.miniAppAPI
import me.madhead.tyzenhaus.launcher.fly.routes.webhook
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

/**
 * Initializes and configures the application.
 */
fun Application.tyzenhaus() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Compression)

    install(Koin) {
        modules(
            configModule(environment.config),
            metricsModule,
            telegramModule,
            jsonModule,
            pipelineModule,
            dbModule,
        )
    }

    install(MicrometerMetrics) {
        this.registry = this@tyzenhaus.get<PrometheusMeterRegistry>()
    }

    routing {
        webhook()
        metrics()
        miniApp()
        miniAppAPI()
    }
}
