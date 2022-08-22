package com.shapcode.lunch.core.util.cache

import androidx.collection.LruCache

class MemoryCache<T: Any> : Cache<T> {
    private val data = LruCache<String, T>(10)
    override suspend fun get(key: String): T? = data[key]
    override suspend fun put(key: String, value: T): T? = data.put(key, value)
    override suspend fun remove(key: String): T? = data.remove(key)
    override suspend fun clear() = data.evictAll()
}