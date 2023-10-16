package me.madhead.tyzenhaus.launcher.fly.koin

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.types.chat.ExtendedBot
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val telegramModule = module {
    single<TelegramBot> {
        telegramBot(get<ApplicationConfig>().property("telegram.token").getString())
    }

    single<ExtendedBot> {
        val bot = get<TelegramBot>()

        runBlocking { bot.getMe() }
    }
}
