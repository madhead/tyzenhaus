package me.madhead.tyzenhaus.core.currencies

/**
 * Lists currencies used in transactions of the group.
 */
class ChatCurrenciesService {
    /**
     * Lists currencies used in transactions of the [group].
     */
    suspend fun groupCurrencies(group: Long): List<String> {
        return listOf("USD", "EUR")
    }
}
