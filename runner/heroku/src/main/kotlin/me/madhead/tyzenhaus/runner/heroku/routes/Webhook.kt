package me.madhead.tyzenhaus.runner.heroku.routes

import dev.inmo.tgbotapi.types.update.abstracts.UpdateDeserializationStrategy
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.post
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import org.apache.logging.log4j.LogManager
import org.koin.ktor.ext.inject

/**
 * Routes for [Telegram Bot API webhooks](https://core.telegram.org/bots/api#setwebhook).
 */
@KtorExperimentalAPI
fun Route.webhook() {
    val logger = LogManager.getLogger("me.madhead.tyzenhaus.runner.heroku.routes.Webhook")
    val json by inject<Json>()
    val pipeline by inject<UpdateProcessingPipeline>()

    post(application.environment.config.property("telegram.token").getString()) {
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
