package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.balance.Balance
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForAmount
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForConfirmation
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.BalanceRepository
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.TransactionRepository
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

/**
 * Store the transaction.
 */
class ConfirmationOKCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
    private val transactionRepository: TransactionRepository,
    private val balanceRepository: BalanceRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK_OK = "confirmation:ok"

        private val logger = LogManager.getLogger(ConfirmationOKCallbackQueryUpdateProcessor::class.java)!!
    }

    @Suppress("LongMethod")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null
        @Suppress("NAME_SHADOWING")
        val dialogState = dialogState as? WaitingForConfirmation ?: return null

        if (callbackQuery.data != CALLBACK_OK) return null

        if (dialogState.userId != update.userId) return {
            requestsExecutor.answerCallbackQuery(
                callbackQuery = callbackQuery,
                text = I18N(groupConfig?.language)["expense.response.confirmation.wrongUser"],
            )
        }

        return {
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
            val share = transaction.amount.setScale(@Suppress("MagicNumber") 6, RoundingMode.HALF_UP) /
                transaction.recipients.size.toBigDecimal()

            currencyBalance[transaction.payer] = (currencyBalance[transaction.payer] ?: BigDecimal.ZERO) + transaction.amount
            transaction.recipients.forEach { recipient ->
                currencyBalance[recipient] = (currencyBalance[recipient] ?: BigDecimal.ZERO) - share
            }
            groupBalance[transaction.currency] = currencyBalance

            balanceRepository.save(balance.copy(balance = groupBalance))
            transactionRepository.save(transaction)
            dialogStateRepository.delete(update.groupId, dialogState.userId)

            val members = (transaction.recipients + transaction.payer).toSet()
            val chatMembers = members.map { requestsExecutor.getChatMemberSafe(ChatId(update.groupId), UserId(it)) }
            val from = "[${chatMembers.first { it.user.id.chatId == transaction.payer }.displayName.escapeMarkdownV2Common()}]" +
                "(tg://user?id=${transaction.payer})"
            val to = transaction.recipients.joinToString(", ") { recipient ->
                "[${chatMembers.first { it.user.id.chatId == recipient }.displayName.escapeMarkdownV2Common()}]" +
                    "(tg://user?id=$recipient)"
            }
            val amount = "${transaction.amount.setScale(2, RoundingMode.HALF_UP)} ${transaction.currency}".escapeMarkdownV2Common()

            requestsExecutor.editMessageText(
                chat = callbackQuery.message.chat,
                messageId = callbackQuery.message.messageId,
                text = I18N(groupConfig?.language)[
                    "expense.response.success",
                    from,
                    to,
                    amount,
                ],
                parseMode = MarkdownV2,
                replyMarkup = null,
            )
        }
    }
}
