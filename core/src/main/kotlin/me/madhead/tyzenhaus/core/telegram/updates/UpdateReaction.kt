package me.madhead.tyzenhaus.core.telegram.updates

import com.github.insanusmokrassar.TelegramBotAPI.types.update.abstracts.Update

/**
 * A reaction for an [updates][Update].
 */
typealias UpdateReaction = suspend () -> Unit
