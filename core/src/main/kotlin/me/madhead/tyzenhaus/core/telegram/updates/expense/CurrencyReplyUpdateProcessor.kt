package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.ChatMember.abstracts.ChatMember
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ForceReply
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
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
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForCurrency
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
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
        val members = groupConfig?.members ?: return null

        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForCurrency ?: return null

        return if ((dialogState.messageId == message.replyTo?.messageId) && (dialogState.userId == update.userId)) {
            logger.debug("Processing currency reply from {} in {}", update.userId, update.groupId)

            val content = message.content as? TextContent ?: return {
                val amountRequestMessage = requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig.language)["expense.response.currency.textPlease"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = ForceReply(
                        selective = true,
                    ),
                )

                dialogStateRepository.save(WaitingForAmount(update.groupId, update.userId, amountRequestMessage.messageId))
            }

            val currency = content.text

            {
                logger.debug("{} provided expense currency ({}) in {}", update.userId, currency, update.groupId)

                val chatMembers = members.map { requestsExecutor.getChatMember(ChatId(update.groupId), UserId(it)) }

                logger.debug("Members of {}: {}", update.groupId, chatMembers)

                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig.language)["expense.action.participants"],
                    parseMode = MarkdownV2,
                    replyToMessageId = message.messageId,
                    replyMarkup = InlineKeyboardMarkup(
                        chatMembers
                            .map {
                                listOf(CallbackDataInlineKeyboardButton(it.callback(), it.callbackData()))
                            }
                    )
                )
                dialogStateRepository.save(
                    WaitingForParticipants(
                        update.groupId,
                        update.userId,
                        dialogState.amount,
                        currency,
                    )
                )
            }
        } else null
    }

    private fun ChatMember.callback(): String = user.firstName +
        (user.lastName.takeUnless { it.isBlank() }?.let { " $it" } ?: "") +
        (user.username?.username?.let { " ($it)" } ?: "")

    private fun ChatMember.callbackData(): String = user.id.chatId.toString()
}
