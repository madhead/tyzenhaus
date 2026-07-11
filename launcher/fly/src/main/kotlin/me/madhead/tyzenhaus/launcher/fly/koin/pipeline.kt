package me.madhead.tyzenhaus.launcher.fly.koin

import dev.inmo.tgbotapi.types.chat.ExtendedBot
import me.madhead.tyzenhaus.core.debts.DebtsCalculator
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessingPipeline
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
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
import me.madhead.tyzenhaus.core.telegram.updates.history.HistoryCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.lang.LangCallbackQueryUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.lang.LangCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.policy.PrivacyCommandUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.supergroup.SupergroupChatCreatedUpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.support.IDCommandUpdateProcessor
import org.koin.dsl.bind
import org.koin.dsl.module

val pipelineModule = module {
    single {
        DebtsCalculator()
    }

    single {
        WelcomeMessageUpdateProcessor(
            id = get<ExtendedBot>().id,
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        StartCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    } bind UpdateProcessor::class
    single {
        HelpCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    } bind UpdateProcessor::class
    single {
        IDCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    } bind UpdateProcessor::class
    single {
        ExpenseCommandUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        DebtsCommandUpdateProcessor(
            requestsExecutor = get(),
            balanceRepository = get(),
            debtsCalculator = get(),
        )
    } bind UpdateProcessor::class
    single {
        AmountReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
            groupCurrenciesService = get(),
        )
    } bind UpdateProcessor::class
    single {
        CurrencyReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        TitleReplyUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        ParticipantCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        DoneCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        ConfirmationOKCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            transactionManager = get(),
            dialogStateRepository = get(),
            transactionRepository = get(),
            balanceRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        ConfirmationCancelCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            dialogStateRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        LangCommandUpdateProcessor(
            requestsExecutor = get(),
        )
    } bind UpdateProcessor::class
    single {
        LangCallbackQueryUpdateProcessor(
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        ParticipateCommandUpdateProcessor(
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        SupergroupChatCreatedUpdateProcessor(
            supergroupRepository = get(),
            meterRegistry = get(),
        )
    } bind UpdateProcessor::class
    single {
        HistoryCommandUpdateProcessor(
            requestsExecutor = get(),
            me = get(),
            apiTokenRepository = get(),
        )
    } bind UpdateProcessor::class
    single {
        PrivacyCommandUpdateProcessor(
            requestsExecutor = get(),
            me = get(),
        )
    } bind UpdateProcessor::class

    single {
        UpdateProcessingPipeline(
            getAll<UpdateProcessor>(),
            get(),
            get(),
        )
    }
}
