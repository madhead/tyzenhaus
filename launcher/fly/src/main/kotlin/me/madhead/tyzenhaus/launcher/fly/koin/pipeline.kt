package me.madhead.tyzenhaus.launcher.fly.koin

import dev.inmo.tgbotapi.types.ChatId
import io.ktor.server.config.ApplicationConfig
import me.madhead.tyzenhaus.core.currencies.ChatCurrenciesService
import me.madhead.tyzenhaus.core.debts.DebtsCalculator
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.expense.AmountReplyUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ConfirmationCancelCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.expense.ConfirmationOKCallbackQueryUpdateProcessor
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
import me.madhead.tyzenhaus.core.telegram.updates.supergroup.SupergroupChatCreatedUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.support.IDCommandUpdateProcessor
import org.koin.dsl.module

val pipelineModule = module {
    single {
        ChatCurrenciesService(
            balanceRepository = get(),
        )
    }
    single {
        DebtsCalculator()
    }

    single {
        WelcomeMessageUpdateProcessor(
            id = ChatId(get<ApplicationConfig>().property("telegram.token").getString().substringBefore(":").toLong()),
            requestsExecutor = get(),
            groupConfigRepository = get(),
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
            dialogStateRepository = get(),
        )
    }
    single {
        DebtsCommandUpdateProcessor(
            requestsExecutor = get(),
            balanceRepository = get(),
            debtsCalculator = get(),
        )
    }
    single {
        AmountReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
            chatCurrenciesService = get(),
        )
    }
    single {
        CurrencyReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    }
    single {
        TitleReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    }
    single {
        ParticipantCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    }
    single {
        DoneCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    }
    single {
        ConfirmationOKCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
            transactionRepository = get(),
            balanceRepository = get(),
        )
    }
    single {
        ConfirmationCancelCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
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
            groupConfigRepository = get(),
        )
    }
    single {
        ParticipateCommandUpdateProcessor(
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    }
    single {
        SupergroupChatCreatedUpdateProcessor(
            supergroupRepository = get(),
            meterRegistry = get(),
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
                get<ConfirmationOKCallbackQueryUpdateProcessor>(),
                get<ConfirmationCancelCallbackQueryUpdateProcessor>(),
                get<LangCommandUpdateProcessor>(),
                get<LangCallbackQueryUpdateProcessor>(),
                get<ParticipateCommandUpdateProcessor>(),
                get<SupergroupChatCreatedUpdateProcessor>(),
            ),
            get(),
            get(),
        )
    }
}
