package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.dialog.state.DialogState
import me.madhead.tyzenhaus.entity.group.config.GroupConfig
import me.madhead.tyzenhaus.repository.DialogStateRepository
import me.madhead.tyzenhaus.repository.GroupConfigRepository
import org.apache.logging.log4j.LogManager

/**
 * A pipeline of [UpdateProcessor]s.
 * Basically, the heart of all input processing of the bot: every update is passed to [process] and processed by one
 * of the [UpdateProcessor]s, the first whose [UpdateProcessor.process] returned non-null [UpdateReaction].
 */
class UpdateProcessingPipeline(
    private val processors: List<UpdateProcessor>,
    private val groupConfigRepository: GroupConfigRepository,
    private val dialogStateRepository: DialogStateRepository,
) {
    companion object {
        private val logger = LogManager.getLogger(UpdateProcessingPipeline::class.java)!!
    }

    /**
     * Process the [update][Update].
     *
     * First, chat id is extracted from the update.
     * Tyzenhaus is intended to be used in groups, so chat id will represent a group.
     *
     * Second, group's [config][GroupConfig] and [dialog state][DialogState] are resolved from the DB.
     * This pair of classes represents server-side state of a dialog with a user of a group.
     * Config is used to store group-wide settings and state is used in FSM.
     *
     * Third, suitable [processor][UpdateProcessor] is selected based on the update, config and state.
     * Suitable processor is the first one from the list of processors whose [process][UpdateProcessor.process] returned non-null.
     * If there is no suitable processor, the update is ignored.
     *
     * Finally, the update is processed.
     */
    suspend fun process(update: Update) {
        logger.debug("Processing update: {}", update)

        val groupConfig = groupConfigRepository.get(update.groupId)
        val dialogState = dialogStateRepository.get(update.groupId, update.userId)

        logger.info("Chat ID: {}", update.groupId)
        logger.info("Config: {}", groupConfig)
        logger.info("Dialog state: {}", dialogState)

        processors
            .mapNotNull { it.process(update, groupConfig, dialogState) }
            .also { reactions ->
                logger.debug("Reactions ({}): {}", reactions.size, reactions.map { it::class })
                if (reactions.size != 1) {
                    logger.warn("No suitable processor found or found more than one")
                }
            }
            .singleOrNull()
            ?.invoke()
    }
}
