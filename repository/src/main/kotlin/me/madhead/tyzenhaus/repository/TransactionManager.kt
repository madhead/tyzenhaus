package me.madhead.tyzenhaus.repository

/**
 * Runs a block of repository operations in a single database transaction.
 *
 * Repository calls made inside [block][transaction] share one connection and are committed atomically when it returns,
 * or rolled back if it throws. Calls outside a [transaction] keep their previous auto-commit behaviour, and nested
 * [transaction] calls join the enclosing one.
 */
interface TransactionManager {
    /**
     * Runs [block] in a single database transaction and returns its result, committing on success and rolling back if
     * it throws.
     */
    suspend fun <T> transaction(block: suspend () -> T): T
}
