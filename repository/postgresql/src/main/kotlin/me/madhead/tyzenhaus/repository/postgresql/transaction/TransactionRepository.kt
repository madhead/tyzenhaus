package me.madhead.tyzenhaus.repository.postgresql.transaction

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import javax.sql.DataSource
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.entity.transaction.TransactionsPage
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams
import me.madhead.tyzenhaus.repository.postgresql.PostgreSqlRepository
import org.apache.logging.log4j.LogManager

/**
 * PostgreSQL repository for [transactions][Transaction].
 */
class TransactionRepository(dataSource: DataSource)
    : me.madhead.tyzenhaus.repository.TransactionRepository, PostgreSqlRepository(dataSource) {
    companion object {
        private val logger = LogManager.getLogger(TransactionRepository::class.java)!!
    }

    @Suppress("NestedBlockDepth")
    override suspend fun get(id: Long): Transaction? {
        logger.debug("get {}", id)

        return withConnection { connection ->
            connection
                .prepareStatement("""
                    SELECT "id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp"
                    FROM "transaction"
                    WHERE "id" = ?;
                """.trimIndent())
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            resultSet.toTransaction()
                        } else {
                            null
                        }
                    }
                }
        }
    }

    override suspend fun save(entity: Transaction) {
        logger.debug("save {}", entity)

        withConnection { connection ->
            if (entity.id == null) {
                connection
                    .prepareStatement("""
                            INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
                            VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?);
                    """.trimIndent())
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.groupId)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, entity.payer)
                        preparedStatement.setArray(
                            @Suppress("MagicNumber") 3,
                            connection.createArrayOf("bigint", entity.recipients.toTypedArray())
                        )
                        preparedStatement.setBigDecimal(@Suppress("MagicNumber") 4, entity.amount)
                        preparedStatement.setString(@Suppress("MagicNumber") 5, entity.currency)
                        preparedStatement.setString(@Suppress("MagicNumber") 6, entity.title)
                        preparedStatement.setTimestamp(@Suppress("MagicNumber") 7, Timestamp.from(entity.timestamp))

                        preparedStatement.executeUpdate()
                    }
            } else {
                connection
                    .prepareStatement("""
                            INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                            ON CONFLICT ("id")
                                DO UPDATE SET "group_id"   = EXCLUDED."group_id",
                                              "payer"      = EXCLUDED."payer",
                                              "recipients" = EXCLUDED."recipients",
                                              "amount"     = EXCLUDED."amount",
                                              "currency"   = EXCLUDED."currency",
                                              "title"      = EXCLUDED."title",
                                              "timestamp"  = EXCLUDED."timestamp";
                    """.trimIndent())
                    .use { preparedStatement ->
                        preparedStatement.setLong(@Suppress("MagicNumber") 1, entity.id!!)
                        preparedStatement.setLong(@Suppress("MagicNumber") 2, entity.groupId)
                        preparedStatement.setLong(@Suppress("MagicNumber") 3, entity.payer)
                        preparedStatement.setArray(
                            @Suppress("MagicNumber") 4,
                            connection.createArrayOf("bigint", entity.recipients.toTypedArray())
                        )
                        preparedStatement.setBigDecimal(@Suppress("MagicNumber") 5, entity.amount)
                        preparedStatement.setString(@Suppress("MagicNumber") 6, entity.currency)
                        preparedStatement.setString(@Suppress("MagicNumber") 7, entity.title)
                        preparedStatement.setTimestamp(@Suppress("MagicNumber") 8, Timestamp.from(entity.timestamp))

                        preparedStatement.executeUpdate()
                    }
            }
        }
    }

    override suspend fun groupCurrencies(groupId: Long): List<String> {
        logger.debug("groupCurrencies {}", groupId)

        return withConnection { connection ->
            connection
                .prepareStatement("""SELECT DISTINCT("currency") FROM "transaction" WHERE "group_id" = ?;""")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, groupId)
                    preparedStatement.executeQuery().use { resultSet ->
                        resultSet.toCurrencies()
                    }
                }
        }
    }

    override suspend fun search(groupId: Long, params: TransactionsSearchParams): TransactionsPage {
        logger.debug("search {} {}", groupId, params)

        return withConnection { connection ->
            val (sql, binders) = connection.searchQuery(groupId, params)

            connection
                .prepareStatement(sql)
                .use { preparedStatement ->
                    binders.forEachIndexed { index, binder -> binder(preparedStatement, index + 1) }
                    preparedStatement.executeQuery().use { resultSet ->
                        resultSet.toTransactionsPage(params.limit)
                    }
                }
        }
    }
}

@Suppress("MagicNumber")
private fun Connection.searchQuery(groupId: Long, params: TransactionsSearchParams): Pair<String, List<(PreparedStatement, Int) -> Unit>> {
    val conditions = mutableListOf("(\"group_id\" = ?)")
    val binders = mutableListOf<(PreparedStatement, Int) -> Unit>({ statement, index -> statement.setLong(index, groupId) })

    params.title?.let { title ->
        conditions += """("title" ILIKE ? ESCAPE '\')"""
        binders += { statement, index -> statement.setString(index, "%${title.escapeLike()}%") }
    }
    if (params.participants.isNotEmpty()) {
        conditions += """("payer" = ANY(?) OR "recipients" && ?)"""

        val participants = createArrayOf("bigint", params.participants.toTypedArray())

        binders += { statement, index -> statement.setArray(index, participants) }
        binders += { statement, index -> statement.setArray(index, participants) }
    }
    params.amountFrom?.let { amount ->
        conditions += """("amount" >= ?)"""
        binders += { statement, index -> statement.setBigDecimal(index, amount) }
    }
    params.amountTo?.let { amount ->
        conditions += """("amount" <= ?)"""
        binders += { statement, index -> statement.setBigDecimal(index, amount) }
    }
    if (params.currencies.isNotEmpty()) {
        conditions += """("currency" = ANY(?))"""

        val currencies = createArrayOf("varchar", params.currencies.toTypedArray())

        binders += { statement, index -> statement.setArray(index, currencies) }
    }
    params.dateFrom?.let { date ->
        conditions += """("timestamp" >= ?)"""
        binders += { statement, index -> statement.setTimestamp(index, Timestamp.from(date)) }
    }
    params.dateTo?.let { date ->
        conditions += """("timestamp" <= ?)"""
        binders += { statement, index -> statement.setTimestamp(index, Timestamp.from(date)) }
    }
    params.cursor?.let { cursor ->
        conditions += """(("timestamp", "id") < (?, ?))"""
        binders += { statement, index -> statement.setTimestamp(index, Timestamp.from(cursor.timestamp)) }
        binders += { statement, index -> statement.setLong(index, cursor.id) }
    }
    binders += { statement, index -> statement.setInt(index, params.limit + 1) }

    val sql = """
        SELECT "id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp"
        FROM "transaction"
        WHERE ${conditions.joinToString(" AND ")}
        ORDER BY "timestamp" DESC, "id" DESC
        LIMIT ?;
    """.trimIndent()

    return sql to binders
}

private fun String.escapeLike(): String = this
    .replace("""\""", """\\""")
    .replace("""%""", """\%""")
    .replace("""_""", """\_""")
