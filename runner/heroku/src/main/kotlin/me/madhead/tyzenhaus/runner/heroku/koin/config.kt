package me.madhead.tyzenhaus.runner.heroku.koin

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.koin.dsl.module

@KtorExperimentalAPI
@Suppress("UndocumentedPublicFunction")
fun configModule(config: ApplicationConfig) = module {
    single {
        config
    }
}
