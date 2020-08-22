package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import me.madhead.tyzenhaus.entity.groupstate.GroupState

interface UpdateProcessor {
    suspend fun accept(update: Update, groupConfig: GroupConfig? = null, groupState: GroupState? = null): Boolean

    suspend fun process(update: Update, groupConfig: GroupConfig? = null, groupState: GroupState? = null)
}
