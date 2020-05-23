package by.dev.madhead.tyzenhaus.runners.heroku.koin

import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.telegramBot
import org.koin.dsl.module

fun telegramModule(token: String) = module {
    single {
        telegramBot(token)
    }
}
