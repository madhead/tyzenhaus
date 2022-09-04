package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager

/**
 * /participate command handler.
 */
class ParticipateCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val groupConfigRepository: GroupConfigRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(ParticipateCommandUpdateProcessor::class.java)!!
    }

    @Suppress("DuplicatedCode")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        if (content.textSources.none { "participate" == (it as? BotCommandTextSource)?.command }) return null

        return {
            logger.debug("{} joins in {}", update.userId, update.groupId)

            val newGroupConfig = groupConfig ?: GroupConfig(update.data.chat.id.chatId)

            CoroutineScope(coroutineContext + Dispatchers.IO).launch {
                groupConfigRepository.save(newGroupConfig.copy(members = newGroupConfig.members + update.userId))
            }

            requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["participate.response.ok"],
                replyToMessageId = message.messageId,
                parseMode = MarkdownV2,
            )
        }
    }
}
