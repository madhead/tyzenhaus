package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ForceReply
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForTitle
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * New expense flow: currency entered.
 */
class TitleReplyUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(TitleReplyUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val members = groupConfig?.members ?: return null
        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForTitle ?: return null

        if (dialogState.messageId != message.replyTo?.messageId) return null

        if (dialogState.userId != update.userId) return {
            requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig.language)["expense.response.title.wrongUser"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
            )
        }

        logger.debug("Processing title reply from {} in {}", update.userId, update.groupId)

        val content = message.content as? TextContent ?: return {
            val titleRequestMessage = requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig.language)["expense.response.title.textPlease"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
                replyMarkup = ForceReply(
                    selective = true,
                ),
            )

            dialogStateRepository.save(
                WaitingForTitle(update.groupId, update.userId, titleRequestMessage.messageId, dialogState.amount, dialogState.currency)
            )
        }

        val title = content.text

        return {
            logger.debug("{} provided title in {}", update.userId, update.groupId)

            val chatMembers = members.map { requestsExecutor.getChatMember(ChatId(update.groupId), UserId(it)) }

            logger.debug("Members of {}: {}", update.groupId, chatMembers)

            val participantsMessage = requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig.language)["expense.action.participants"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
                replyMarkup = replyMarkup(chatMembers, emptySet(), groupConfig, false)
            )

            dialogStateRepository.save(
                WaitingForParticipants(
                    update.groupId,
                    update.userId,
                    participantsMessage.messageId,
                    dialogState.amount,
                    dialogState.currency,
                    title,
                )
            )
        }
    }
}
