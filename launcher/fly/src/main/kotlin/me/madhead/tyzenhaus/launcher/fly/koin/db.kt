package me.madhead.tyzenhaus.launcher.fly.koin

import io.ktor.server.config.ApplicationConfig
import java.net.URI
import javax.sql.DataSource
import me.madhead.tyzenhaus.repository.postgresql.balance.BalanceRepository
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.transaction.TransactionRepository
import org.koin.dsl.module
import org.postgresql.ds.PGSimpleDataSource

val dbModule = module {
    single<DataSource> {
        val databaseUrl = URI(get<ApplicationConfig>().property("database.url").getString())

        return@single PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://${databaseUrl.host}:${databaseUrl.port}${databaseUrl.path}")
            user = databaseUrl.userInfo.split(":")[0]
            password = databaseUrl.userInfo.split(":")[1]
        }
    }

    single {
        GroupConfigRepository(get())
    }

    single {
        DialogStateRepository(get())
    }

    single {
        TransactionRepository(get())
    }

    single {
        BalanceRepository(get())
    }
}
