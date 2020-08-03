package me.madhead.tyzenhaus.runners.heroku.routes

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.UpdateDeserializationStrategy
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.post
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.koin.ktor.ext.inject

@KtorExperimentalAPI
fun Route.webhook() {
    val logger = LogManager.getLogger("me.madhead.tyzenhaus.runners.heroku.routes.Webhook")
    val bot by inject<RequestsExecutor>()
    val json by inject<Json>()

    post(application.environment.config.property("telegram.token").getString()) {
        val payload = call.receiveText()

        logger.debug("Request payload: {}", payload)

        val update = json.parse(UpdateDeserializationStrategy, payload)

        logger.info("Update object: {}", update)

        try {
            (update as? MessageUpdate)?.let { messageUpdate ->
                bot.sendMessage(
                        chat = messageUpdate.data.chat,
                        text = "Your message was received successfully!"
                )
            }
        } catch (e: Exception) {
            logger.error("Failure!", e)
        }

        call.respond(HttpStatusCode.OK)
    }
}
