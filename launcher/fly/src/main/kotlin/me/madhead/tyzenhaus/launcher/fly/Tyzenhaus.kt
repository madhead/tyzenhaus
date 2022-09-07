package me.madhead.tyzenhaus.launcher.fly

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.types.BotCommand
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import me.madhead.tyzenhaus.launcher.fly.koin.configModule
import me.madhead.tyzenhaus.launcher.fly.koin.dbModule
import me.madhead.tyzenhaus.launcher.fly.koin.jsonModule
import me.madhead.tyzenhaus.launcher.fly.koin.pipelineModule
import me.madhead.tyzenhaus.launcher.fly.koin.telegramModule
import me.madhead.tyzenhaus.launcher.fly.routes.webhook
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

/**
 * [Ktor-based](https://ktor.io) Tyzenhaus runner.
 */
@Suppress("unused")
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
                BotCommand("lang", "Change language"),
                BotCommand("participate", "Register yourself for expense tracking in this group"),
                BotCommand("expense", "Add a shared expense"),
                BotCommand("debts", "Show all the debts"),
            )
        }
    }
}
