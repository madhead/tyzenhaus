package me.madhead.tyzenhaus.core.telegram.updates.expense

import dev.inmo.tgbotapi.bot.RequestsExecutor
import dev.inmo.tgbotapi.extensions.api.answers.answerCallbackQuery
import dev.inmo.tgbotapi.types.CallbackQuery.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.update.CallbackQueryUpdate
import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.core.telegram.updates.UpdateProcessor
import me.madhead.tyzenhaus.core.telegram.updates.UpdateReaction
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.dialog.state.WaitingForParticipants
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.i18.I18N
import me.madhead.tyzenhaus.repository.DialogStateRepository
import org.apache.logging.log4j.LogManager

/**
 * Check / uncheck a participant.
 */
class DoneCallbackQueryUpdateProcessor(
    private val requestsExecutor: RequestsExecutor,
    private val dialogStateRepository: DialogStateRepository,
) : UpdateProcessor {
    companion object {
        const val CALLBACK = "participants:done"

        private val logger = LogManager.getLogger(DoneCallbackQueryUpdateProcessor::class.java)!!
    }

    override suspend fun process(update: Update, groupConfig: GroupConfig?, dialogState: DialogState?): UpdateReaction? {
        @Suppress("NAME_SHADOWING")
        val update = update as? CallbackQueryUpdate ?: return null
        val callbackQuery = update.data as? MessageDataCallbackQuery ?: return null
        val members = groupConfig?.members ?: return null

        return if (callbackQuery.data.startsWith(CALLBACK) && (dialogState is WaitingForParticipants)) {
            {
                logger.debug("Creating shared expense in {}!", groupConfig.id)
            }
        } else if (callbackQuery.data.startsWith(CALLBACK) && (dialogState !is WaitingForParticipants)) {
            {
                requestsExecutor.answerCallbackQuery(
                    callbackQuery = callbackQuery,
                    text = I18N(groupConfig.language)["expense.response.participants.done.wrongState"],
                )
            }
        } else null
    }
}
