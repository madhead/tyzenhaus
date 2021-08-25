package me.madhead.tyzenhaus.entity.transaction

import java.math.BigDecimal
import java.time.Instant

/**
 * Shared expense.
 */
data class Transaction(
    val id: Long?,
    val groupId: Long,
    val payer: Long,
    val recipients: Set<Long>,
    val amount: BigDecimal,
    val currency: String,
    val title: String? = null,
    val timestamp: Instant = Instant.now(),
)
