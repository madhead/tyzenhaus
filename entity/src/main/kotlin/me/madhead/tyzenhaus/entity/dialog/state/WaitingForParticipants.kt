package me.madhead.tyzenhaus.entity.dialog.state

import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer

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
    val title: String,
    val participants: Set<Long> = emptySet(),
) : DialogState
