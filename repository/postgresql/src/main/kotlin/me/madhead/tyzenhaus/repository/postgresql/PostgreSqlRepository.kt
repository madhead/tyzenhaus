package me.madhead.tyzenhaus.repository.postgresql

import me.madhead.tyzenhaus.repository.Repository
import javax.sql.DataSource

/**
 * Base class for PostgreSQL repositories.
 */
abstract class PostgreSqlRepository<ID, T>(
        protected val dataSource: DataSource
) : Repository<ID, T>
