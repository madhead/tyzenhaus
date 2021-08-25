package me.madhead.tyzenhaus.core.currencies

import me.madhead.tyzenhaus.repository.BalanceRepository

/**
 * Lists currencies used in transactions of the group.
 */
class ChatCurrenciesService(
    private val balanceRepository: BalanceRepository,
) {
    /**
     * Lists currencies used in transactions of the [group].
     */
    suspend fun groupCurrencies(group: Long): List<String> {
        return balanceRepository
            .get(group)
            ?.balance
            ?.keys
            ?.takeUnless { it.isEmpty() }
            ?.toList()
            ?: listOf("USD", "EUR", "RUB")
    }
}
