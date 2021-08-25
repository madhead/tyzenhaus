package me.madhead.tyzenhaus.entity.balance

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.madhead.tyzenhaus.entity.serializers.BigDecimalSerializer
import java.math.BigDecimal

private typealias Currency = String
private typealias User = Long
private typealias UserBalance = @Serializable(BigDecimalSerializer::class) BigDecimal
private typealias UserBalances = Map<User, UserBalance>
private typealias GroupBalance = Map<Currency, UserBalances>

/**
 * Group's per-currency & per-user balances.
 */
@Serializable
data class Balance(
    val groupId: Long,

    @Transient
    val version: Long = 1,

    val balance: GroupBalance = emptyMap(),
)
