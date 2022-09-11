package me.madhead.tyzenhaus.launcher.fly.routes

import dev.inmo.tgbotapi.types.update.abstracts.UpdateDeserializationStrategy
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import org.apache.logging.log4j.LogManager
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Bot API webhooks](https://core.telegram.org/bots/api#setwebhook).
 */
fun Route.webhook() {
    val logger = LogManager.getLogger("me.madhead.tyzenhaus.launcher.fly.routes.Webhook")
    val config by inject<ApplicationConfig>()
    val port = config.property("deployment.port").getString().toInt()
    val json by inject<Json>()
    val pipeline by inject<UpdateProcessingPipeline>()

    post(config.property("telegram.token").getString()) {
        if (call.request.local.port != port) {
            return@post
        }

        try {
            val payload = call.receiveText()

            logger.debug("Request payload: {}", payload)

            val update = json.decodeFromString(UpdateDeserializationStrategy, payload)

            logger.info("Update object: {}", update)

            pipeline.process(update)
        } catch (ignored: Exception) {
            logger.error("Failed to handle the request", ignored)
        }

        call.respond(HttpStatusCode.OK)
    }
}
