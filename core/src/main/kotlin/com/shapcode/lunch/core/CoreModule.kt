package com.shapcode.lunch.core

import android.content.Context
import com.shapcode.lunch.core.model.Restaurant
import com.shapcode.lunch.core.repo.FavoriteRepo
import com.shapcode.lunch.core.repo.RestaurantRepo
import com.shapcode.lunch.core.repo.impl.DefaultFavoriteRepo
import com.shapcode.lunch.core.util.cache.Cache
import com.shapcode.lunch.core.repo.impl.DefaultRestaurantRepo
import com.shapcode.lunch.core.util.cache.MemoryCache
import com.shapcode.lunch.core.util.cache.PreferencesCache
import com.shapcode.lunch.core.util.converter.Converter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepoModule {
    @Binds
    abstract fun bindRestaurantRepo(
        restaurantRepo: DefaultRestaurantRepo
    ): RestaurantRepo

    @Binds
    abstract fun bindFavoriteRepo(
        favoriteRepo: DefaultFavoriteRepo
    ): FavoriteRepo
}

@Module
@InstallIn(SingletonComponent::class)
object CoreCacheModule {
    @Provides
    @Singleton
    fun bindRestaurantCache(): Cache<List<Restaurant>> {
        return MemoryCache()
    }

    @Provides
    @Singleton
    fun bindFavoriteCache(@ApplicationContext context: Context): Cache<Set<String>> {
        return PreferencesCache(
            context.getSharedPreferences("favorites", Context.MODE_PRIVATE),
            object: Converter<Set<String>> {
                override fun toString(value: Set<String>): String {
                    return value.joinToString(",")
                }
                override fun fromString(value: String): Set<String> {
                    return value.split(",").toSet()
                }
            }
        )
    }
}