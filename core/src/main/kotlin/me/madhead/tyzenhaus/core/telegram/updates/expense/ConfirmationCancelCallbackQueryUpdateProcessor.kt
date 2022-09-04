package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForConfirmation
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * Cancel the transaction.
 */
class ConfirmationCancelCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK_CANCEL = "confirmation:cancel"

        private val logger = LogManager.getLogger(ConfirmationCancelCallbackQueryUpdateProcessor::class.java)!!
    }

    @Suppress("LongMethod")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null

        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForConfirmation ?: return null

        if (callbackQuery.data != CALLBACK_CANCEL) return null

        if (dialogState.userId != update.userId) return {
            requestsExecutor.answerCallbackQuery(
                callbackQuery = callbackQuery,
                text = I18N(groupConfig?.language)["expense.response.confirmation.wrongUser"],
            )
        }

        return {
            logger.debug("{} cancels transaction in {}", update.userId, update.groupId)

            dialogStateRepository.delete(update.groupId, dialogState.userId)
            requestsExecutor.editMessageText(
                chat = callbackQuery.message.chat,
                messageId = callbackQuery.message.messageId,
                text = I18N(groupConfig?.language)["expense.response.canceled"],
                parseMode = MarkdownV2,
                replyMarkup = null,
            )
        }
    }
}
