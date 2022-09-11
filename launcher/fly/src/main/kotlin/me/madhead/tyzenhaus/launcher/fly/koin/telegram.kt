package me.madhead.tyzenhaus.launcher.fly.koin

import dev.inmo.tgbotapi.extensions.api.telegramBot
import io.ktor.server.config.ApplicationConfig
import org.koin.dsl.module

val telegramModule = module {
    single {
        telegramBot(get<ApplicationConfig>().property("telegram.token").getString())
    }
}
