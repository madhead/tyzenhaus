package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User requested a language change.
 */
@Serializable
@SerialName("ChangingLanguage")
data class ChangingLanguage(
        override val groupId: Long,
        override val userId: Long
) : DialogState
