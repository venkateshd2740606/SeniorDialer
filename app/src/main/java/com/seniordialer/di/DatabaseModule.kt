package com.seniordialer.di

import android.content.Context
import androidx.room.Room
import com.seniordialer.data.local.database.SeniorDialerDatabase
import com.seniordialer.data.local.database.dao.FavoriteContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SeniorDialerDatabase =
        Room.databaseBuilder(context, SeniorDialerDatabase::class.java, "seniordialer.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideFavoriteContactDao(db: SeniorDialerDatabase): FavoriteContactDao = db.favoriteContactDao()
}
