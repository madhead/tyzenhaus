package me.madhead.tyzenhaus.runner.heroku.koin

import dev.inmo.tgbotapi.types.ChatId
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import me.madhead.tyzenhaus.core.currencies.ChatCurrenciesService
import me.madhead.tyzenhaus.core.debts.DebtsCalculator
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.expense.AmountReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ConfirmationCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.CurrencyReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.DebtsCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.DoneCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ExpenseCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ParticipantCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ParticipateCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.TitleReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.help.HelpCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.help.StartCommandUpdateProcessor
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
        ChatCurrenciesService(
            balanceRepository = get<BalanceRepository>(),
        )
    }
    single {
        DebtsCalculator()
    }

    single {
        WelcomeMessageUpdateProcessor(
            id = ChatId(get<ApplicationConfig>().property("telegram.token").getString().substringBefore(":").toLong()),
            requestsExecutor = get(),
            groupConfigRepository = get<GroupConfigRepository>(),
        )
    }
    single {
        StartCommandUpdateProcessor(
            requestsExecutor = get(),
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
        DebtsCommandUpdateProcessor(
            requestsExecutor = get(),
            balanceRepository = get<BalanceRepository>(),
            debtsCalculator = get(),
        )
    }
    single {
        AmountReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
            chatCurrenciesService = get(),
        )
    }
    single {
        CurrencyReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
        )
    }
    single {
        TitleReplyUpdateProcessor(
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
        ConfirmationCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get<DialogStateRepository>(),
            transactionRepository = get<TransactionRepository>(),
            balanceRepository = get<BalanceRepository>(),
        )
    }
    single {
        LangCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    }
    single {
        LangCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
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
                get<StartCommandUpdateProcessor>(),
                get<HelpCommandUpdateProcessor>(),
                get<IDCommandUpdateProcessor>(),
                get<ExpenseCommandUpdateProcessor>(),
                get<DebtsCommandUpdateProcessor>(),
                get<AmountReplyUpdateProcessor>(),
                get<CurrencyReplyUpdateProcessor>(),
                get<TitleReplyUpdateProcessor>(),
                get<ParticipantCallbackQueryUpdateProcessor>(),
                get<DoneCallbackQueryUpdateProcessor>(),
                get<ConfirmationCallbackQueryUpdateProcessor>(),
                get<LangCommandUpdateProcessor>(),
                get<LangCallbackQueryUpdateProcessor>(),
                get<ParticipateCommandUpdateProcessor>(),
            ),
            get<GroupConfigRepository>(),
            get<DialogStateRepository>(),
        )
    }
}
