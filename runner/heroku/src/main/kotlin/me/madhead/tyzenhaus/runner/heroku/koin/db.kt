package me.madhead.tyzenhaus.runner.heroku.koin

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import me.madhead.tyzenhaus.repository.postgresql.group.config.GroupConfigRepository
import me.madhead.tyzenhaus.repository.postgresql.group.state.GroupStateRepository
import org.koin.dsl.module
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import javax.sql.DataSource

@KtorExperimentalAPI
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
        GroupStateRepository(get())
    }
}
