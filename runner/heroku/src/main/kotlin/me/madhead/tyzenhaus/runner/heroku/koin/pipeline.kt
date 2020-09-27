package me.madhead.tyzenhaus.runner.heroku.koin

import com.github.insanusmokrassar.TelegramBotAPI.types.ChatId
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.UnstableDefault
import me.madhead.tyzenhaus.core.telegram.updates.HelpCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCommandCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.WelcomeMessageUpdateProcessor
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.group.state.GroupStateRepository
import org.koin.dsl.module

@KtorExperimentalAPI
@UnstableDefault
val pipelineModule = module {
    single {
        WelcomeMessageUpdateProcessor(
                id = ChatId(get<ApplicationConfig>().property("telegram.botId").getString().toLong()),
                requestsExecutor = get(),
        )
    }
    single {
        HelpCommandUpdateProcessor(
                requestsExecutor = get(),
        )
    }
    single {
        LangCommandUpdateProcessor(
                requestsExecutor = get(),
        )
    }
    single {
        LangCommandCallbackQueryUpdateProcessor(
                requestsExecutor = get(),
                groupConfigRepository = get<GroupConfigRepository>(),
        )
    }
    single {
        UpdateProcessingPipeline(
                listOf(
                        get<WelcomeMessageUpdateProcessor>(),
                        get<HelpCommandUpdateProcessor>(),
                        get<LangCommandUpdateProcessor>(),
                        get<LangCommandCallbackQueryUpdateProcessor>(),
                ),
                get<GroupConfigRepository>(),
                get<GroupStateRepository>(),
        )
    }
}
