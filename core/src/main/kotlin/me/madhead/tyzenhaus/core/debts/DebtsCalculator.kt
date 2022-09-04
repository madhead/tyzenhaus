package me.madhead.tyzenhaus.core.debts

import java.math.BigDecimal
import me.madhead.tyzenhaus.entity.balance.Balance

/**
 * Calculates debts given group's balance.
 */
class DebtsCalculator {
    /**
     * Calculates debts given group's [balance].
     */
    fun calculate(balance: Balance): List<Debt> {
        val result = mutableListOf<Debt>()

        balance.balance.forEach { (currency, userBalances) ->
            val balances = userBalances.toMutableMap()
            val users = balances.keys.toList()
            var i = 0
            var j = 0

            while ((i < users.size) && (j < users.size)) {
                if (balances[users[i]]!! <= BigDecimal.ZERO) {
                    i++
                } else if (balances[users[j]]!! >= BigDecimal.ZERO) {
                    j++
                } else {
                    val m = balances[users[i]]!!.abs().min(balances[users[j]]!!.abs())

                    result.add(Debt(m, currency, users[j], users[i]))

                    balances[users[i]] = balances[users[i]]!! - m
                    balances[users[j]] = balances[users[j]]!! + m
                }
            }
        }

        return result.sortedWith(
            compareBy<Debt> { it.currency }.thenByDescending { it.amount }
        )
    }
}
