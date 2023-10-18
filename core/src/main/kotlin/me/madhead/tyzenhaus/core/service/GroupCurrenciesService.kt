package me.madhead.tyzenhaus.core.service

import me.madhead.tyzenhaus.repository.BalanceRepository

/**
 * Lists currencies used in transactions of the group.
 */
class GroupCurrenciesService(
    private val balanceRepository: BalanceRepository,
) {
    /**
     * Lists currencies used in transactions of the [group].
     */
    fun groupCurrencies(group: Long): List<String> {
        return balanceRepository
            .get(group)
            ?.balance
            ?.keys
            ?.takeUnless { it.isEmpty() }
            ?.toList()
            ?: listOf("USD", "EUR", "RUB")
    }
}
