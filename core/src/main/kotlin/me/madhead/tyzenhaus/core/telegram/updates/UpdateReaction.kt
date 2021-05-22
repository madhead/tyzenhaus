package me.madhead.tyzenhaus.core.telegram.updates

import dev.inmo.tgbotapi.types.update.abstracts.Update

/**
 * A reaction for an [updates][Update].
 */
typealias UpdateReaction = suspend () -> Unit
