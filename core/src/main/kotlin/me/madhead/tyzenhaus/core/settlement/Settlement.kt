package me.madhead.tyzenhaus.core.settlement

import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

// Verhoeff, T. (2004). Settling multiple debts efficiently : an invitation to computing science. Informatics in Education, 3(1), 105-126.
// What can be precomputed in production on each transaction is balance. It's the most important thing.
// Minimizing the total transferred amount is easy
// Minimizing the total number of transfers is hard
fun main() {
    val balances = generateTransactions()
    val keys = balances.keys.toList()
    var i = 0
    var j = 0

    while ((i < keys.size) && (j < keys.size)) {
        if (balances[i]!! <= 0) {
            i++
        } else if (balances[j]!! >= 0) {
            j++
        } else {
            val m = min(abs(balances[i]!!), abs(balances[j]!!))

            println("$i pays $j \$$m")

            balances[i] = balances[i]!! - m
            balances[j] = balances[j]!! + m
            balances.dump()
        }
    }
}

private fun generateTransactions(): MutableMap<Int, Int> {
    val parties = Random.nextInt(3, 10 + 1)
    val transactions = Random.nextInt(5, 100 + 1)
    val balances = mutableMapOf<Int, Int>()

    println("$parties parties generated $transactions transactions")

    for (i in 0..transactions) {
        val from = Random.nextInt(parties)
        val to = run {
            var result = Random.nextInt(parties)

            while (result == from) {
                result = Random.nextInt(parties)
            }

            result
        }
        val amount = Random.nextInt(100)

        println("$from is paying $to \$$amount")

        balances[from] = (balances[from] ?: 0) - amount
        balances[to] = (balances[to] ?: 0) + amount
    }

    assert(balances.values.sum() == 0)
    balances.dump()

    return balances
}

fun Map<Int, Int>.dump() {
    println("BALANCES:")
    this.toSortedMap().forEach { (party, balance) ->
        println("$party's balance: \$$balance")
    }
}
