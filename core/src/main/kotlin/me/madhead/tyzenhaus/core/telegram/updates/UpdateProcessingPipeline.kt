package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.BaseMessageUpdate
import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupconfig.GroupConfig
import me.madhead.tyzenhaus.entity.groupstate.GroupState
import me.madhead.tyzenhaus.repository.Repository
import org.apache.logging.log4j.LogManager

class UpdateProcessingPipeline(
        private val processors: List<UpdateProcessor>,
        private val groupConfigRepository: Repository<Long, GroupConfig>,
        private val groupStateRepository: Repository<Long, GroupState>,
) {
    companion object {
        val logger = LogManager.getLogger(WelcomeMessageUpdateProcessor::class.java)!!
    }

    suspend fun process(update: Update) {
        logger.debug("Processing update: {}", update)

        val chatId = extractChatId(update)
        val groupConfig = groupConfigRepository.get(chatId)
        val groupState = groupStateRepository.get(chatId)

        logger.debug("Chat ID: {}", chatId)
        logger.debug("Config: {}", groupConfig)
        logger.debug("State: {}", groupState)

        val suitable = processors.filter { it.accept(update, groupConfig, groupState) }

        logger.debug("Suitable processors: {}", suitable)

        when (suitable.size) {
            0 -> {
                logger.info("No suitable processors found")
            }
            1 -> {
                logger.info("Found single suitable processor")

                suitable[0].process(update, groupConfig, groupState)
            }
            else -> throw IllegalArgumentException("More than one processor wants to process the update!")
        }
    }

    private fun extractChatId(update: Update): Long {
        return when (update) {
            is BaseMessageUpdate -> {
                update.data.chat.id.chatId
            }
            else -> throw IllegalArgumentException("Unknown update type")
        }
    }
}
