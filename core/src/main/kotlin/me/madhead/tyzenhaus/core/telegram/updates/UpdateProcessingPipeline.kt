package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update
import me.madhead.tyzenhaus.entity.groupstate.GroupState

class UpdateProcessingPipeline(
        private val processors: List<UpdateProcessor>
) {
    suspend fun process(update: Update, groupState: GroupState?) {
        val suitable = processors.filter { it.accepts(update, groupState) }

        when (suitable.size) {
            0 -> {
            }
            1 -> {
                suitable[0].process(update, groupState)
            }
            else -> throw IllegalArgumentException("More than one processor wants to process the update!")
        }
    }
}
