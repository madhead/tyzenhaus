package me.madhead.tyzenhaus.core.service

import me.madhead.tyzenhaus.repository.TransactionRepository

/**
 * Lists currencies used in transactions of the group.
 */
class GroupCurrenciesService(
    private val transactionRepository: TransactionRepository
) {
    /**
     * Lists currencies used in transactions of the [group].
     */
    fun groupCurrencies(group: Long): List<String>? {
        return transactionRepository.groupCurrencies(group).takeUnless { it.isEmpty() }
    }
}
