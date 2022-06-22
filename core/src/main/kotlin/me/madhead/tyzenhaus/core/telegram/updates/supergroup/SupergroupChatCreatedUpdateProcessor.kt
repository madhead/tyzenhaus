package me.madhead.tyzenhaus.core.telegram.updates.supergroup

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.message.ChatEvents.SupergroupChatCreated
import dev.inmo.tgbotapi.types.message.abstracts.ChatEventMessage
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import org.apache.logging.log4j.LogManager

/**
 * Handles supergroup updates.
 */
class SupergroupChatCreatedUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(SupergroupChatCreatedUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? ChatEventMessage<*> ?: return null
        val event = message.chatEvent as? SupergroupChatCreated ?: return null
        val migratedFrom = event.migratedFrom as? ChatId ?: return null

        return {
            logger.warn("Upgrading {} to a supergroup from {}", message.chat.id.chatId, migratedFrom.chatId)
        }
    }
}
