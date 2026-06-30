package com.seniordialer.di

import com.seniordialer.data.repository.FavoriteContactRepositoryImpl
import com.seniordialer.data.repository.PreferencesRepositoryImpl
import com.seniordialer.domain.repository.FavoriteContactRepository
import com.seniordialer.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindFavoriteContactRepository(impl: FavoriteContactRepositoryImpl): FavoriteContactRepository

    @Binds @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
