package me.madhead.tyzenhaus.runners.heroku.koin

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.koin.dsl.module

@UnstableDefault
val jsonModule = module {
    single {
        Json(JsonConfiguration(
                ignoreUnknownKeys = true
        ))
    }
}
