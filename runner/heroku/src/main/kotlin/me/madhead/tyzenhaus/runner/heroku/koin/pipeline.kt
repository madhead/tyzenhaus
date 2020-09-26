package me.madhead.tyzenhaus.runner.heroku.koin

import com.github.insanusmokrassar.TelegramBotAPI.types.ChatId
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import me.madhead.tyzenhaus.core.telegram.updates.HelpCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.WelcomeMessageUpdateProcessor
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import org.koin.dsl.module

@KtorExperimentalAPI
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
                dialogStateRepository = get<DialogStateRepository>(),
        )
    }
    single {
        LangCallbackQueryUpdateProcessor(
                requestsExecutor = get(),
                dialogStateRepository = get<DialogStateRepository>(),
                groupConfigRepository = get<GroupConfigRepository>(),
        )
    }
    single {
        UpdateProcessingPipeline(
                listOf(
                        get<WelcomeMessageUpdateProcessor>(),
                        get<HelpCommandUpdateProcessor>(),
                        get<LangCommandUpdateProcessor>(),
                        get<LangCallbackQueryUpdateProcessor>(),
                ),
                get<GroupConfigRepository>(),
                get<DialogStateRepository>(),
        )
    }
}
