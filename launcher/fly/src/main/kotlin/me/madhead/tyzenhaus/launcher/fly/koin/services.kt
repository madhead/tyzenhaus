package me.madhead.tyzenhaus.launcher.fly.koin

import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.service.GroupMembersService
import me.madhead.tyzenhaus.core.service.TransactionsSearchService
import org.koin.dsl.module

val serviceModule = module {
    single {
        GroupCurrenciesService(
            transactionRepository = get(),
        )
    }

    single {
        GroupMembersService(
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    }

    single {
        TransactionsSearchService(
            transactionRepository = get()
        )
    }
}
