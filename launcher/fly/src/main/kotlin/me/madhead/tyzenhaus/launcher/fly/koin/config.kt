package me.madhead.tyzenhaus.launcher.fly.koin

import io.ktor.server.config.ApplicationConfig
import org.koin.dsl.module

@Suppress("UndocumentedPublicFunction")
fun configModule(config: ApplicationConfig) = module {
    single {
        config
    }
}
