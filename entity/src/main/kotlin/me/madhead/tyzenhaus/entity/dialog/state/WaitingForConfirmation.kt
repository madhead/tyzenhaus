package me.madhead.tyzenhaus.entity.dialog.state

import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer

/**
 * Expense flow: waiting for confirmation.
 */
@Serializable
@SerialName("WaitingForConfirmation")
data class WaitingForConfirmation(
    override val groupId: Long,
    override val userId: Long,
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    val currency: String,
    val title: String,
    val participants: Set<Long> = emptySet(),
) : DialogState
