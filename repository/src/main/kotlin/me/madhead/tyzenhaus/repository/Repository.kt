package me.madhead.tyzenhaus.repository

interface Repository<ID, T> {
    fun get(id: ID): T?
}
