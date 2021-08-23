package me.madhead.tyzenhaus.repository.postgresql

import javax.sql.DataSource

/**
 * Base class for PostgreSQL repositories.
 */
abstract class PostgreSqlRepository(
    protected val dataSource: DataSource
)
