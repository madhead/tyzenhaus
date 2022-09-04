package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import java.math.RoundingMode
import java.time.Instant
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForConfirmation
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository

/**
 * Finalize the transaction.
 */
class DoneCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK = "participants:done"
    }

    @Suppress("LongMethod", "DuplicatedCode")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null

        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForParticipants ?: return null

        if (callbackQuery.data != CALLBACK) return null

        if ((dialogState.userId != update.userId) || (dialogState.messageId != callbackQuery.message.messageId)) return {
            requestsExecutor.answerCallbackQuery(
                callbackQuery = callbackQuery,
                text = I18N(groupConfig?.language)["expense.response.participants.wrongUser"],
            )
        }

        val transaction = Transaction(
            id = null,
            groupId = dialogState.groupId,
            payer = dialogState.userId,
            recipients = dialogState.participants,
            amount = dialogState.amount,
            currency = dialogState.currency,
            title = dialogState.title,
            timestamp = Instant.now(),
        )
        val members = (transaction.recipients + transaction.payer).toSet()
        val chatMembers = members.map { requestsExecutor.getChatMemberSafe(ChatId(update.groupId), UserId(it)) }
        val from = "[${chatMembers.first { it.user.id.chatId == transaction.payer }.displayName.escapeMarkdownV2Common()}]" +
            "(tg://user?id=${transaction.payer})"
        val to = transaction.recipients.joinToString(", ") { recipient ->
            "[${chatMembers.first { it.user.id.chatId == recipient }.displayName.escapeMarkdownV2Common()}]" +
                "(tg://user?id=$recipient)"
        }
        val amount = "${transaction.amount.setScale(2, RoundingMode.HALF_UP)} ${transaction.currency}".escapeMarkdownV2Common()

        return {
            requestsExecutor.editMessageReplyMarkup(
                message = callbackQuery.message,
                replyMarkup = null,
            )
            requestsExecutor.sendMessage(
                chatId = callbackQuery.message.chat.id,
                text = I18N(groupConfig?.language)[
                    "expense.action.confirmation",
                    from,
                    to,
                    amount,
                ],
                parseMode = MarkdownV2,
                replyMarkup = InlineKeyboardMarkup(
                    listOf(
                        listOf(
                            CallbackDataInlineKeyboardButton(
                                I18N(groupConfig?.language)["expense.response.confirmation.ok"],
                                ConfirmationOKCallbackQueryUpdateProcessor.CALLBACK_OK
                            ),
                            CallbackDataInlineKeyboardButton(
                                I18N(groupConfig?.language)["expense.response.confirmation.cancel"],
                                ConfirmationCancelCallbackQueryUpdateProcessor.CALLBACK_CANCEL
                            )
                        )
                    )
                )
            )

            dialogStateRepository.save(
                WaitingForConfirmation(
                    groupId = dialogState.groupId,
                    userId = dialogState.userId,
                    amount = dialogState.amount,
                    currency = dialogState.currency,
                    title = dialogState.title,
                    participants = dialogState.participants,
                )
            )
        }
    }
}
