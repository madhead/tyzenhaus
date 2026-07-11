package me.madhead.tyzenhaus.launcher.fly.modules

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.routing
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.launcher.fly.koin.configModule
import me.madhead.tyzenhaus.launcher.fly.koin.dbModule
import me.madhead.tyzenhaus.launcher.fly.koin.jsonModule
import me.madhead.tyzenhaus.launcher.fly.koin.metricsModule
import me.madhead.tyzenhaus.launcher.fly.koin.pipelineModule
import me.madhead.tyzenhaus.launcher.fly.koin.serviceModule
import me.madhead.tyzenhaus.launcher.fly.koin.telegramModule
import me.madhead.tyzenhaus.launcher.fly.routes.metrics
import me.madhead.tyzenhaus.launcher.fly.routes.miniApp
import me.madhead.tyzenhaus.launcher.fly.routes.miniAppAPI
import me.madhead.tyzenhaus.launcher.fly.routes.webhook
import me.madhead.tyzenhaus.launcher.fly.security.api
import me.madhead.tyzenhaus.repository.APITokenRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository
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
            dbModule,
            serviceModule,
            pipelineModule,
            metricsModule,
            telegramModule,
            jsonModule,
        )
    }

    install(ContentNegotiation) {
        json(this@tyzenhaus.get<Json>())
    }

    install(MicrometerMetrics) {
        this.registry = this@tyzenhaus.get<PrometheusMeterRegistry>()
    }

    install(Authentication) {
        api(
            this@tyzenhaus.environment.config.property("telegram.token").getString(),
            this@tyzenhaus.get<APITokenRepository>(),
            this@tyzenhaus.get<GroupConfigRepository>(),
            this@tyzenhaus.get<Json>(),
        )
    }

    routing {
        webhook()
        metrics()
        miniApp()
        miniAppAPI()
    }
}
