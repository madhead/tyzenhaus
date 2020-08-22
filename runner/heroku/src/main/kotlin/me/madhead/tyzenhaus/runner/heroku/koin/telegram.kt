package me.madhead.tyzenhaus.runner.heroku.koin

import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.telegramBot
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.koin.dsl.module

@KtorExperimentalAPI
val telegramModule = module {
    single {
        telegramBot(get<ApplicationConfig>().property("telegram.token").getString())
    }
}
