package me.madhead.tyzenhaus.repository.postgres

import me.madhead.tyzenhaus.repository.Repository
import javax.sql.DataSource

abstract class PostgreSqlRepository<ID, T>(
        protected val dataSource: DataSource
) : Repository<ID, T>
