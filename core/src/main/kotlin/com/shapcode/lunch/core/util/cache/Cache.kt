package com.shapcode.lunch.core.util.cache

interface Cache<T: Any> {
    suspend fun get(key: String): T?
    suspend fun put(key: String, value: T): T?
    suspend fun remove(key: String): T?
    suspend fun clear()
}