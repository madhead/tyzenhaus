package me.madhead.tyzenhaus.entity.dialog.state

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Dialog's state for FSM-like core logic.
 */
interface DialogState {
    companion object {
        val serializers = SerializersModule {
            polymorphic(DialogState::class) {
                subclass(ChangingLanguage::class)
            }
        }
    }

    val groupId: Long

    val userId: Long
}
