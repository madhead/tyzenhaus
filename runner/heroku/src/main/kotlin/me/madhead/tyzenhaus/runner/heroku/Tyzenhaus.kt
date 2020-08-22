package me.madhead.tyzenhaus.runner.heroku

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.bot.setMyCommands
import com.github.insanusmokrassar.TelegramBotAPI.types.BotCommand
import io.ktor.application.Application
import io.ktor.application.ApplicationStarted
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import me.madhead.tyzenhaus.runner.heroku.koin.configModule
import me.madhead.tyzenhaus.runner.heroku.koin.dbModule
import me.madhead.tyzenhaus.runner.heroku.koin.jsonModule
import me.madhead.tyzenhaus.runner.heroku.koin.pipelineModule
import me.madhead.tyzenhaus.runner.heroku.koin.telegramModule
import me.madhead.tyzenhaus.runner.heroku.routes.webhook
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

/**
 * [Ktor-based](https://ktor.io) Tyzenhaus runner.
 */
@UnstableDefault
@KtorExperimentalAPI
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Compression)
    install(Koin) {
        modules(
                configModule(environment.config),
                telegramModule,
                jsonModule,
                pipelineModule,
                dbModule,
        )
    }

    routing {
        webhook()
    }

    environment.monitor.subscribe(ApplicationStarted) {
        val bot by inject<RequestsExecutor>()

        runBlocking {
            bot.setMyCommands(
                    BotCommand("help", "How to use the bot"),
                    BotCommand("start", "Main menu"),
                    BotCommand("lang", "Change language"),
            )
        }
    }
}
