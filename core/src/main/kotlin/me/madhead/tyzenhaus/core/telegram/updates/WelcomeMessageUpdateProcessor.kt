package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.extensions.api.send.sendMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.ChatIdentifier
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.MarkdownV2
import com.github.insanusmokrassar.TelegramBotAPI.types.message.ChatEvents.NewChatMembers
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.ChatEventMessage
import com.github.insanusmokrassar.TelegramBotAPI.types.update.MessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
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
        val message = update.data as? ChatEventMessage ?: return null
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
