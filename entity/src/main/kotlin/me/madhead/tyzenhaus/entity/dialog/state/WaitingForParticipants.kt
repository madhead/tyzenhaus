package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer
import java.math.BigDecimal

/**
 * Expense flow: waiting for currency input.
 */
@Serializable
@SerialName("WaitingForParticipants")
data class WaitingForParticipants(
    override val groupId: Long,
    override val userId: Long,
    val messageId: Long,
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    val currency: String,
    val participants: Set<Long> = emptySet(),
) : DialogState
