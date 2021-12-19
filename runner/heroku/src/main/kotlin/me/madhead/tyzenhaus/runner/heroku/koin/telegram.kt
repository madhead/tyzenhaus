package me.madhead.tyzenhaus.runner.heroku.koin

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import io.ktor.config.ApplicationConfig
import org.koin.dsl.module

val telegramModule = module {
    single {
        telegramBot(get<ApplicationConfig>().property("telegram.token").getString())
    }
}
