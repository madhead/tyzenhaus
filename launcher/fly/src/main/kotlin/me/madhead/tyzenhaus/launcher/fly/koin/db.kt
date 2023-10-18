package me.madhead.tyzenhaus.launcher.fly.koin

import io.ktor.server.config.ApplicationConfig
import java.net.URI
import javax.sql.DataSource
import me.madhead.tyzenhaus.repository.APITokenRepository
import me.madhead.tyzenhaus.repository.BalanceRepository
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import me.madhead.tyzenhaus.repository.MetricsRepository
import me.madhead.tyzenhaus.repository.SupergroupRepository
import me.madhead.tyzenhaus.repository.TransactionRepository
import org.koin.dsl.module
import org.postgresql.ds.PGSimpleDataSource
import me.madhead.tyzenhaus.repository.postgresql.api.token.APITokenRepository as PostgreSQLAPITokenRepository
import me.madhead.tyzenhaus.repository.postgresql.balance.BalanceRepository as PostgreSQLBalanceRepository
import me.madhead.tyzenhaus.repository.postgresql.dialog.state.DialogStateRepository as PostgreSQLDialogStateRepository
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository as PostgreSQLGroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.metrics.MetricsRepository as PostgreSQLMetricsRepository
import me.madhead.tyzenhaus.repository.postgresql.supergroup.SupergroupRepository as PostgreSQLSupegroupRepository
import me.madhead.tyzenhaus.repository.postgresql.transaction.TransactionRepository as PostgreSQLTransactionRepository

val dbModule = module {
    single<DataSource> {
        val databaseUrl = URI(get<ApplicationConfig>().property("database.url").getString())

        return@single PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://${databaseUrl.host}:${databaseUrl.port}${databaseUrl.path}")
            user = databaseUrl.userInfo.split(":")[0]
            password = databaseUrl.userInfo.split(":")[1]
        }
    }

    single<GroupConfigRepository> {
        PostgreSQLGroupConfigRepository(get())
    }

    single<DialogStateRepository> {
        PostgreSQLDialogStateRepository(get())
    }

    single<TransactionRepository> {
        PostgreSQLTransactionRepository(get())
    }

    single<BalanceRepository> {
        PostgreSQLBalanceRepository(get())
    }

    single<MetricsRepository> {
        PostgreSQLMetricsRepository(get())
    }

    single<SupergroupRepository> {
        PostgreSQLSupegroupRepository(get())
    }

    single<APITokenRepository> {
        PostgreSQLAPITokenRepository(get())
    }
}
