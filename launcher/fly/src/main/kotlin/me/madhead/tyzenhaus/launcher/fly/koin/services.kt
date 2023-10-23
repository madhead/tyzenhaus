package me.madhead.tyzenhaus.launcher.fly.koin

import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.service.GroupMembersService
import org.koin.dsl.module

val serviceModule = module {
    single {
        GroupCurrenciesService(
            balanceRepository = get(),
        )
    }

    single {
        GroupMembersService(
            requestsExecutor = get(),
            groupConfigRepository = get(),
        )
    }
}
