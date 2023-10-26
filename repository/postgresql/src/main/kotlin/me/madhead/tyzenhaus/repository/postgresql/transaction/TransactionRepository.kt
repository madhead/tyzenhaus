package me.madhead.tyzenhaus.repository.postgresql.transaction

import java.sql.Timestamp
import javax.sql.DataSource
import me.madhead.tyzenhaus.entity.transaction.Transaction
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
    override fun get(id: Long): Transaction? {
        logger.debug("get {}", id)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("""SELECT * FROM "transaction" WHERE "id" = ?;""")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, id)
                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            return@get resultSet.toTransaction()
                        } else {
                            return@get null
                        }
                    }
                }
        }
    }

    override fun save(entity: Transaction) {
        logger.debug("save {}", entity)

        dataSource.connection.use { connection ->
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

    override fun groupCurrencies(groupId: Long): List<String> {
        logger.debug("groupCurrencies {}", groupId)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("""SELECT DISTINCT("currency") FROM "transaction" WHERE "group_id" = ?;""")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, groupId)
                    preparedStatement.executeQuery().use { resultSet ->
                        return@groupCurrencies resultSet.toCurrencies()
                    }
                }
        }
    }

    override fun search(groupId: Long): List<Transaction> {
        logger.debug("search {}", groupId)

        dataSource.connection.use { connection ->
            connection
                .prepareStatement("""SELECT * FROM "transaction" WHERE "group_id" = ? ORDER BY "timestamp" DESC;""")
                .use { preparedStatement ->
                    preparedStatement.setLong(@Suppress("MagicNumber") 1, groupId)
                    preparedStatement.executeQuery().use { resultSet ->
                        return@search resultSet.toTransactions()
                    }
                }
        }
    }
}
