package me.madhead.tyzenhaus.core.telegram.updates.expenses

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.buttons.ForceReply
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForCurrency
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForTitle
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * New expense flow: currency entered.
 */
class CurrencyReplyUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(CurrencyReplyUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null

        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForCurrency ?: return null

        return if ((dialogState.messageId == message.replyTo?.messageId) && (dialogState.userId == update.userId)) {
            logger.debug("Processing currency reply from {} in {}", update.userId, update.groupId)

            val content = message.content as? TextContent ?: return {
                val currencyRequestMessage = requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["expense.response.currency.textPlease"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = ForceReply(
                        selective = true,
                    ),
                )

                dialogStateRepository.save(
                    WaitingForCurrency(update.groupId,
                        update.userId,
                        currencyRequestMessage.messageId,
                        dialogState.amount
                    )
                )
            }

            val currency = content.text

            {
                logger.debug("{} provided expense currency ({}) in {}", update.userId, currency, update.groupId)

                val titleMessage = requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["expense.action.title"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = ForceReply(
                        selective = true,
                    ),
                )

                dialogStateRepository.save(
                    WaitingForTitle(
                        update.groupId,
                        update.userId,
                        titleMessage.messageId,
                        dialogState.amount,
                        currency,
                    )
                )
            }
        } else null
    }
}

