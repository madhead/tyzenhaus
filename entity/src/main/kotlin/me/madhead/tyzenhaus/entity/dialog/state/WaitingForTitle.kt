package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer
import java.math.BigDecimal

/**
 * Expense flow: waiting for title input.
 */
@Serializable
@SerialName("WaitingForTitle")
data class WaitingForTitle(
    override val groupId: Long,
    override val userId: Long,
    val messageId: Long,
    @Serializable(BigDecimalSerializer::class)
    val amount: BigDecimal,
    val currency: String,
) : DialogState
