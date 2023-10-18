package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.buttons.ReplyForce
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import java.math.BigDecimal
import me.madhead.tyzenhaus.core.service.GroupCurrenciesService
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForCurrency
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * New expense flow: amount entered.
 */
class AmountReplyUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
    private val groupCurrenciesService: GroupCurrenciesService,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(AmountReplyUpdateProcessor::class.java)!!
    }

    @Suppress("ReturnCount", "LongMethod")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null

        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForAmount ?: return null

        if (dialogState.messageId != message.replyTo?.messageId) return null

        if (dialogState.userId != update.userId) return {
            requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["expense.response.amount.wrongUser"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
            )
        }

        logger.debug("Processing amount reply from {} in {}", update.userId, update.groupId)

        val content = message.content as? TextContent ?: return {
            val amountRequestMessage = requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["expense.response.amount.numberPlease"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
                replyMarkup = ReplyForce(
                    selective = true,
                ),
            )

            dialogStateRepository.save(WaitingForAmount(update.groupId, update.userId, amountRequestMessage.messageId))
        }

        val amount = content.text.toBigDecimalOrNull() ?: return {
            val amountRequestMessage = requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["expense.response.amount.numberPlease"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
                replyMarkup = ReplyForce(
                    selective = true,
                ),
            )

            dialogStateRepository.save(WaitingForAmount(update.groupId, update.userId, amountRequestMessage.messageId))
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return {
                val amountRequestMessage = requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["expense.response.amount.nonZeroNumberPlease"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = ReplyForce(
                        selective = true,
                    ),
                )

                dialogStateRepository.save(WaitingForAmount(update.groupId, update.userId, amountRequestMessage.messageId))
            }
        }

        return {
            logger.debug("{} provided expense amount ({}) in {}", update.userId, amount, update.groupId)

            val currencyRequestMessage = requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = I18N(groupConfig?.language)["expense.action.currency"],
                parseMode = MarkdownV2,
                replyToMessageId = message.messageId,
                replyMarkup = dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup(
                    keyboard = groupCurrenciesService
                        .groupCurrencies(update.groupId)
                        .map { listOf(SimpleKeyboardButton(it)) },
                    resizeKeyboard = true,
                    oneTimeKeyboard = true,
                    selective = true,
                )
            )

            dialogStateRepository.save(WaitingForCurrency(update.groupId, update.userId, currencyRequestMessage.messageId, amount))
        }
    }
}
