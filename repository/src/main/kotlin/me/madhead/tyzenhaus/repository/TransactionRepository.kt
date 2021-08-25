package me.madhead.tyzenhaus.repository

import me.madhead.tyzenhaus.entity.transaction.Transaction

/**
 * Transactions repository.
 */
interface TransactionRepository : Repository<Long, Transaction>
