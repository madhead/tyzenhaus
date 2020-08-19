package me.madhead.tyzenhaus.runner.heroku.routes

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.bot.setMyCommands
import com.github.insanusmokrassar.TelegramBotAPI.types.BotCommand
import com.github.insanusmokrassar.TelegramBotAPI.types.ChatId
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.UpdateDeserializationStrategy
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.routing.post
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.WelcomeMessageUpdateProcessor
import org.apache.logging.log4j.LogManager
import org.koin.ktor.ext.inject

@KtorExperimentalAPI
fun Route.webhook() {
    val logger = LogManager.getLogger("me.madhead.tyzenhaus.runner.heroku.routes.Webhook")
    val bot by inject<RequestsExecutor>()
    val json by inject<Json>()
    val pipeline = UpdateProcessingPipeline(
            listOf(
                    WelcomeMessageUpdateProcessor(
                            id = ChatId(application.environment.config.property("telegram.botId").getString().toLong()),
                            requestsExecutor = bot,
                    )
            )
    )

    // Ugh…
    GlobalScope.launch {
        try {
            bot.setMyCommands(
                    BotCommand("help", "How to use the bot"),
                    BotCommand("start", "Main menu"),
                    BotCommand("lang", "Change language"),
            )
        } catch (e: Exception) {
            logger.error("Failed to set commands", e)
        }
    }

    post(application.environment.config.property("telegram.token").getString()) {
        val payload = call.receiveText()

        logger.debug("Request payload: {}", payload)

        val update = json.parse(UpdateDeserializationStrategy, payload)

        logger.info("Update object: {}", update)

        try {
            pipeline.process(update, null)
        } catch (e: Exception) {
            logger.error("Failed to handle the request", e)
        }

        call.respond(HttpStatusCode.OK)
    }
}
