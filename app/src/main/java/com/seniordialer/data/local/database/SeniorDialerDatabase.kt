package com.seniordialer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seniordialer.data.local.database.dao.FavoriteContactDao
import com.seniordialer.data.local.database.entity.FavoriteContactEntity

@Database(entities = [FavoriteContactEntity::class], version = 1, exportSchema = false)
abstract class SeniorDialerDatabase : RoomDatabase() {
    abstract fun favoriteContactDao(): FavoriteContactDao
}
