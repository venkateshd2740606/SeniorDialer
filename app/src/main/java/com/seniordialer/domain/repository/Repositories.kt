package com.seniordialer.domain.repository

import com.seniordialer.domain.model.FavoriteContact
import com.seniordialer.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface FavoriteContactRepository {
    fun observeContacts(): Flow<List<FavoriteContact>>
    suspend fun getContact(id: Long): FavoriteContact?
    suspend fun saveContact(contact: FavoriteContact): Long
    suspend fun deleteContact(id: Long)
    suspend fun count(): Int
    suspend fun reorder(contactIdsInOrder: List<Long>)
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences)
}
