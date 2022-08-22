package com.shapcode.lunch.core.repo.impl

import com.shapcode.lunch.core.repo.FavoriteRepo
import com.shapcode.lunch.core.util.cache.Cache
import javax.inject.Inject

class DefaultFavoriteRepo @Inject constructor(
    private val cache: Cache<Set<String>>,
) : FavoriteRepo {

    override suspend fun addFavorite(id: String): Set<String> {
        val favorites = getAllFavorites() + id
        cache.put("favorites", favorites)
        return favorites
    }

    override suspend fun removeFavorite(id: String): Set<String> {
        val favorites = getAllFavorites() - id
        cache.put("favorites", favorites)
        return favorites
    }

    override suspend fun toggleFavorite(id: String): Set<String> {
        val favorites = getAllFavorites()
        return if (favorites.contains(id)) {
            removeFavorite(id)
        } else {
            addFavorite(id)
        }
    }

    override suspend fun getAllFavorites(): Set<String> {
        return cache.get("favorites") ?: setOf()
    }
}