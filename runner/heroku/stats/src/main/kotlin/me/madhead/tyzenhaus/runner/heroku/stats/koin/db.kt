package me.madhead.tyzenhaus.runner.heroku.stats.koin

import org.koin.dsl.module
import org.postgresql.ds.PGSimpleDataSource
import java.net.URI
import javax.sql.DataSource

val dbModule = module {
    single<DataSource> {
        val databaseUrl = URI(System.getenv("DATABASE_URL")!!)

        return@single PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://${databaseUrl.host}:${databaseUrl.port}${databaseUrl.path}")
            user = databaseUrl.userInfo.split(":")[0]
            password = databaseUrl.userInfo.split(":")[1]
        }
    }
}
