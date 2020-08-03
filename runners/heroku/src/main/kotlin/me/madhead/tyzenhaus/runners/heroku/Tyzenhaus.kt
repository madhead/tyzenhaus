package me.madhead.tyzenhaus.runners.heroku

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.UnstableDefault
import me.madhead.tyzenhaus.runners.heroku.koin.jsonModule
import me.madhead.tyzenhaus.runners.heroku.koin.telegramModule
import me.madhead.tyzenhaus.runners.heroku.routes.webhook
import org.koin.ktor.ext.Koin

@UnstableDefault
@KtorExperimentalAPI
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Compression)
    install(Koin) {
        modules(
                telegramModule(environment.config.property("telegram.token").getString()),
                jsonModule
        )
    }

    routing {
        webhook()
    }
}
