package me.madhead.tyzenhaus.core.telegram.updates.expenses

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.ReplyMarkup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.titleField
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.BalanceRepository
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.TransactionRepository
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.time.Instant

/**
 * Check / uncheck a participant.
 */
class DoneCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
    private val transactionRepository: TransactionRepository,
    private val balanceRepository: BalanceRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK = "participants:done"

        private val logger = LogManager.getLogger(DoneCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null

        return if (callbackQuery.data.startsWith(CALLBACK) && (dialogState is WaitingForParticipants)) {
            {
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

                logger.debug("Creating shared expense: {}", transaction)

                val balance = balanceRepository.get(update.groupId) ?: Balance(update.groupId)
                val groupBalance = balance.balance.toMutableMap()
                val currencyBalance = groupBalance[transaction.currency]?.toMutableMap() ?: mutableMapOf()
                val share = transaction.amount / transaction.recipients.size.toBigDecimal()

                currencyBalance[transaction.payer] = (currencyBalance[transaction.payer] ?: BigDecimal.ZERO) + transaction.amount
                transaction.recipients.forEach { recipient ->
                    currencyBalance[recipient] = (currencyBalance[recipient] ?: BigDecimal.ZERO) - share
                }
                groupBalance[transaction.currency] = currencyBalance

                balanceRepository.save(balance.copy(balance = groupBalance))
                transactionRepository.save(transaction)
                dialogStateRepository.delete(update.groupId, dialogState.userId)

                requestsExecutor.editMessageReplyMarkup(
                    message = callbackQuery.message,
                    replyMarkup = null,
                )
                requestsExecutor.sendMessage(
                    chatId = callbackQuery.message.chat.id,
                    text = I18N(groupConfig?.language)["expense.response.success"],
                    parseMode = MarkdownV2,
                )
            }
        } else if (callbackQuery.data.startsWith(CALLBACK) && (dialogState !is WaitingForParticipants)) {
            {
                requestsExecutor.answerCallbackQuery(
                    callbackQuery = callbackQuery,
                    text = I18N(groupConfig?.language)["expense.response.participants.done.wrongState"],
                )
            }
        } else null
    }
}
