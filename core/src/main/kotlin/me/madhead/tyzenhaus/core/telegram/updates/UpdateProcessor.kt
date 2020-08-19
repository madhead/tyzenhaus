package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupstates.GroupState

interface UpdateProcessor {
    suspend fun accepts(update: Update, groupState: GroupState?): Boolean

    suspend fun process(update: Update, groupState: GroupState?)
}
