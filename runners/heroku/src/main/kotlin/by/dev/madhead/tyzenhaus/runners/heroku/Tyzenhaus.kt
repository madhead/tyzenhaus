package by.dev.madhead.tyzenhaus.runners.heroku

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Compression)

    routing {
        get("/") {
            call.respondText("Hello, world!", ContentType.Text.Plain)
        }
    }
}
