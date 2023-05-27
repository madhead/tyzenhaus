package me.madhead.tyzenhaus.core.telegram.updates.supergroup

import dev.inmo.tgbotapi.types.message.ChatEvents.MigratedToSupergroup
import dev.inmo.tgbotapi.types.message.abstracts.ChatEventMessage
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.micrometer.core.instrument.MeterRegistry
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.repository.SupergroupRepository
import org.apache.logging.log4j.LogManager

/**
 * Handles supergroup updates.
 */
class SupergroupChatCreatedUpdateProcessor(
    private val supergroupRepository: SupergroupRepository,
    meterRegistry: MeterRegistry,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(SupergroupChatCreatedUpdateProcessor::class.java)!!
    }

    private val updates = meterRegistry.counter("tyzenhaus.supergroup.updates")

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? ChatEventMessage<*> ?: return null
        val event = message.chatEvent as? MigratedToSupergroup ?: return null
        val migratedFrom = event.migratedFrom ?: return null

        return {
            logger.warn("Upgrading {} to a supergroup from {}", message.chat.id.chatId, migratedFrom.chatId)

            supergroupRepository.update(migratedFrom.chatId, message.chat.id.chatId)
            updates.increment()
        }
    }
}
