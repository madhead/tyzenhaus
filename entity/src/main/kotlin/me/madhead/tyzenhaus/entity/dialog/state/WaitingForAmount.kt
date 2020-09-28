package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User initiated an expense flow.
 */
@Serializable
@SerialName("WaitingForAmount")
data class WaitingForAmount(
    override val groupId: Long,
    override val userId: Long,
    val messageId: Long,
) : DialogState
