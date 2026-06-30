package com.seniordialer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seniordialer.data.local.database.entity.FavoriteContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteContactDao {
    @Query("SELECT * FROM favorite_contacts ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<FavoriteContactEntity>>

    @Query("SELECT * FROM favorite_contacts WHERE id = :id")
    suspend fun getById(id: Long): FavoriteContactEntity?

    @Query("SELECT COUNT(*) FROM favorite_contacts")
    suspend fun count(): Int

    @Query("SELECT MAX(sortOrder) FROM favorite_contacts")
    suspend fun maxOrder(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: FavoriteContactEntity): Long

    @Update
    suspend fun update(contact: FavoriteContactEntity)

    @Query("DELETE FROM favorite_contacts WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE favorite_contacts SET sortOrder = :order WHERE id = :id")
    suspend fun updateOrder(id: Long, order: Int)
}
