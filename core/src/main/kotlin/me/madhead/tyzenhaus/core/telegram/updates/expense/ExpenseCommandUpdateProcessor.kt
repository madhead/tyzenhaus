package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
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
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * /expense command handler.
 */
class ExpenseCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(ExpenseCommandUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = message.content as? TextContent ?: return null

        return if (content.textSources.any { "expense" == (it as? BotCommandTextSource)?.command }) {
            {
                logger.debug("{} initiated an expense in {}", update.userId, update.groupId)

                val amountRequestMessage = requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["expense.action.amount"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = ForceReply(
                        selective = true,
                    ),
                )

                dialogStateRepository.save(WaitingForAmount(update.groupId, update.userId, amountRequestMessage.messageId))
            }
        } else null
    }
}
