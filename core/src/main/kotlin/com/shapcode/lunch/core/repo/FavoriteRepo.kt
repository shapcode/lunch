package com.shapcode.lunch.core.repo

interface FavoriteRepo {
    suspend fun addFavorite(id: String): Set<String>
    suspend fun removeFavorite(id: String): Set<String>
    suspend fun toggleFavorite(id: String): Set<String>
    suspend fun getAllFavorites(): Set<String>
}