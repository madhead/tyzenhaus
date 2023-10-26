package me.madhead.tyzenhaus.entity.transaction

import java.math.BigDecimal
import java.time.Instant
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer
import me.madhead.tyzenhaus.entity.serializers.InstantSerializer

/**
 * Shared expense.
 */
@Serializable
data class Transaction(
    val id: Long?,
    val groupId: Long,
    val payer: Long,
    val recipients: Set<Long>,
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    val currency: String,
    val title: String? = null,
    @Serializable(InstantSerializer::class)
    val timestamp: Instant = Instant.now(),
)
