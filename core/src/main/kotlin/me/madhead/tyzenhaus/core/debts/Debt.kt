package me.madhead.tyzenhaus.core.debts

import java.math.BigDecimal

/**
 * Represents a debt of [amount] in [currency] from [from] to [to].
 */
data class Debt(
    val amount: BigDecimal,
    val currency: String,
    val from: Long,
    val to: Long,
)
