package me.madhead.tyzenhaus.runner.heroku.koin

import dev.inmo.tgbotapi.types.ChatId
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import me.madhead.tyzenhaus.core.currencies.ChatCurrenciesService
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.expenses.AmountReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expenses.CurrencyReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expenses.DoneCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expenses.ExpenseCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expenses.ParticipantCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expenses.ParticipateCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.help.HelpCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.help.WelcomeMessageUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.lang.LangCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.lang.LangCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.support.IDCommandUpdateProcessor
import me.madhead.tyzenhaus.repository.postgresql.balance.BalanceRepository
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.transaction.TransactionRepository
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
        IDCommandUpdateProcessor(
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
        CurrencyReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
        )
    }
    single {
        ParticipantCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
        )
    }
    single {
        DoneCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
            transactionRepository = get<TransactionRepository>(),
            balanceRepository = get<BalanceRepository>(),
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
                get<IDCommandUpdateProcessor>(),
                get<ExpenseCommandUpdateProcessor>(),
                get<AmountReplyUpdateProcessor>(),
                get<CurrencyReplyUpdateProcessor>(),
                get<ParticipantCallbackQueryUpdateProcessor>(),
                get<DoneCallbackQueryUpdateProcessor>(),
                get<LangCommandUpdateProcessor>(),
                get<LangCallbackQueryUpdateProcessor>(),
                get<ParticipateCommandUpdateProcessor>(),
            ),
            get<GroupConfigRepository>(),
            get<DialogStateRepository>(),
        )
    }
}
