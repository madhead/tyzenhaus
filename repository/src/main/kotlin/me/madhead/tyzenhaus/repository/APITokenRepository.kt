package me.madhead.tyzenhaus.repository

import java.util.UUID
import me.madhead.tyzenhaus.entity.api.token.APIToken

/**
 * API tokens repository.
 */
interface APITokenRepository : Repository<UUID, APIToken>
