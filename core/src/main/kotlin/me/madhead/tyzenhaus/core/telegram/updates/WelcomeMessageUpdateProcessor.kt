package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.message.ChatEvents.NewChatMembers
import dev.inmo.tgbotapi.types.message.abstracts.ChatEventMessage
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager
import java.time.Instant

/**
 * Sends welcome message whenever the bot is added to a group.
 */
class WelcomeMessageUpdateProcessor(
    private val id: ChatIdentifier,
    private val requestsExecutor: RequestsExecutor,
    private val groupConfigRepository: GroupConfigRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(WelcomeMessageUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? ChatEventMessage<*> ?: return null
        val event = message.chatEvent as? NewChatMembers ?: return null

        return if (event.members.any { it.id == id }) {
            {
                logger.debug("Saying welcome in {}", update.data.chat.id.chatId)

                val newGroupConfig = (groupConfig ?: GroupConfig(update.groupId)).copy(
                    invitedBy = update.userId,
                    invitedAt = Instant.now()
                )

                groupConfigRepository.save(newGroupConfig)
                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N()["welcome"],
                    parseMode = MarkdownV2,
                )
            }
        } else null
    }
}
