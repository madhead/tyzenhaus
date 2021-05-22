package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig

/**
 * Telegram [updates][Update] (messages, callbacks, etc.) processor.
 */
interface UpdateProcessor {
    /**
     * Returns a reaction for this [update] or `null` if this processor doesn't want to process this [update].
     */
    suspend fun process(update: Update, groupConfig: GroupConfig? = null, dialogState: DialogState? = null): UpdateReaction?
}
