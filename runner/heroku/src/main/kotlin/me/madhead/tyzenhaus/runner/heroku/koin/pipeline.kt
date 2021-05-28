package me.madhead.tyzenhaus.runner.heroku.koin

import dev.inmo.tgbotapi.types.ChatId
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import me.madhead.tyzenhaus.core.currencies.ChatCurrenciesService
import me.madhead.tyzenhaus.core.telegram.updates.HelpCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.LangCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.ParticipateCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.WelcomeMessageUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.AmountReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ExpenseCommandUpdateProcessor
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import org.koin.dsl.module

@KtorExperimentalAPI
val pipelineModule = module {
    single {
        WelcomeMessageUpdateProcessor(
            id = ChatId(get<ApplicationConfig>().property("telegram.token").getString().substringBefore(":").toLong()),
            requestsExecutor = get(),
            groupConfigRepository = get<GroupConfigRepository>(),
        )
    }
    single {
        HelpCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    }
    single {
        ExpenseCommandUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
        )
    }
    single {
        AmountReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
            chatCurrenciesService = ChatCurrenciesService(),
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
        ParticipateCommandUpdateProcessor(
            requestsExecutor = get(),
            groupConfigRepository = get<GroupConfigRepository>(),
        )
    }
    single {
        UpdateProcessingPipeline(
            listOf(
                get<WelcomeMessageUpdateProcessor>(),
                get<HelpCommandUpdateProcessor>(),
                get<ExpenseCommandUpdateProcessor>(),
                get<AmountReplyUpdateProcessor>(),
                get<LangCommandUpdateProcessor>(),
                get<LangCallbackQueryUpdateProcessor>(),
                get<ParticipateCommandUpdateProcessor>(),
            ),
            get<GroupConfigRepository>(),
            get<DialogStateRepository>(),
        )
    }
}
