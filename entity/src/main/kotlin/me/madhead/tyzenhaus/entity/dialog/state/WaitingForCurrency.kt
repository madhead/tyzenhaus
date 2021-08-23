package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer
import java.math.BigDecimal

/**
 * Expense flow: waiting for currency input.
 */
@Serializable
@SerialName("WaitingForCurrency")
data class WaitingForCurrency(
    override val groupId: Long,
    override val userId: Long,
    val messageId: Long,
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
) : DialogState
