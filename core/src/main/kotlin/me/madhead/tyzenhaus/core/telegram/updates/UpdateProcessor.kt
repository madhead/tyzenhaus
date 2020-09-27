package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.group.state.GroupState

/**
 * Telegram updates (messages, callbacks, etc.) processor.
 */
interface UpdateProcessor {
    /**
     * True if this processor is able to process the update, false otherwise.
     */
    suspend fun accept(update: Update, groupConfig: GroupConfig? = null, groupState: GroupState? = null): Boolean

    /**
     * Process the update.
     */
    suspend fun process(update: Update, groupConfig: GroupConfig? = null, groupState: GroupState? = null)
}
