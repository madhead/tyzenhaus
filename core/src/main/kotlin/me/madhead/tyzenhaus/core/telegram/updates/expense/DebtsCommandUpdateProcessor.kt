package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageEntity.textsources.BotCommandTextSource
import dev.inmo.tgbotapi.types.ParseMode.MarkdownV2
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.update.MessageUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import me.madhead.tyzenhaus.core.debts.DebtsCalculator
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.core.telegram.updates.groupId
import me.madhead.tyzenhaus.core.telegram.updates.userId
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.BalanceRepository
import org.apache.logging.log4j.LogManager
import java.math.RoundingMode

/**
 * /debts command handler.
 */
class DebtsCommandUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val balanceRepository: BalanceRepository,
    private val debtsCalculator: DebtsCalculator,
) : UpdateProcessor {
    companion object {
        private val logger = LogManager.getLogger(DebtsCommandUpdateProcessor::class.java)!!
    }

    @Suppress("ReturnCount")
    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? MessageUpdate ?: return null
        val message = update.data as? CommonMessage<*> ?: return null
        val content = (message as? CommonMessage<*>)?.content as? TextContent ?: return null

        if (content.textSources.none { "debts" == (it as? BotCommandTextSource)?.command }) return null

        logger.debug("{} asked for debts in {}", update.userId, update.groupId)

        val balance = balanceRepository.get(update.groupId)
            ?: return {
                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["debts.response.emptyBalance"],
                    parseMode = MarkdownV2,
                )
            }

        val debts = debtsCalculator.calculate(balance)

        if (debts.isEmpty()) {
            return {
                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["debts.response.emptyBalance"],
                    parseMode = MarkdownV2,
                )
            }
        }

        val members = groupConfig?.members?.plus(debts.flatMap { listOf(it.from, it.to) })

        if (members.isNullOrEmpty()) {
            return {
                requestsExecutor.sendMessage(
                    chatId = update.data.chat.id,
                    text = I18N(groupConfig?.language)["debts.response.noMembers"],
                    parseMode = MarkdownV2,
                )
            }
        }

        val chatMembers = members.map { requestsExecutor.getChatMemberSafe(ChatId(update.groupId), UserId(it)) }
        val debtsMessage = debts
            .joinToString(
                prefix = I18N(groupConfig.language)["debts.response.title"],
                separator = "\n"
            ) { debt ->
                val from = chatMembers.first { it.user.id.chatId == debt.from }
                val to = chatMembers.first { it.user.id.chatId == debt.to }
                val amount = "${debt.amount.setScale(2, RoundingMode.HALF_UP)} ${debt.currency}".escapeMarkdownV2Common()

                I18N(groupConfig.language)[
                    "debts.response.owes",
                    "[${from.displayName.escapeMarkdownV2Common()}](tg://user?id=${debt.from})",
                    "[${to.displayName.escapeMarkdownV2Common()}](tg://user?id=${debt.to})",
                    amount,
                ]
            }

        return {
            requestsExecutor.sendMessage(
                chatId = update.data.chat.id,
                text = debtsMessage,
                parseMode = MarkdownV2,
            )
        }
    }
}
