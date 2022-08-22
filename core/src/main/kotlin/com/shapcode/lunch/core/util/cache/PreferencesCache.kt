package com.shapcode.lunch.core.util.cache

import android.content.SharedPreferences
import com.shapcode.lunch.core.util.converter.Converter

class PreferencesCache<T: Any>(
    private val preferences: SharedPreferences,
    private val converter: Converter<T>,
) : Cache<T> {
    override suspend fun get(key: String): T? {
        return preferences.getString(key, null)?.let {
            converter.fromString(it)
        }
    }
    override suspend fun put(key: String, value: T): T? {
        val original = get(key)
        preferences.edit().putString(key, converter.toString(value)).apply()
        return original
    }
    override suspend fun remove(key: String): T? {
        val original = get(key)
        preferences.edit().remove(key).apply()
        return original
    }
    override suspend fun clear() {
        preferences.edit().clear()
    }

}