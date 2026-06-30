package com.seniordialer.data.repository

import com.seniordialer.data.local.PreferencesDataStore
import com.seniordialer.data.local.database.dao.FavoriteContactDao
import com.seniordialer.data.local.database.entity.FavoriteContactEntity
import com.seniordialer.domain.model.FavoriteContact
import com.seniordialer.domain.model.UserPreferences
import com.seniordialer.domain.repository.FavoriteContactRepository
import com.seniordialer.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteContactRepositoryImpl @Inject constructor(
    private val dao: FavoriteContactDao
) : FavoriteContactRepository {
    override fun observeContacts(): Flow<List<FavoriteContact>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getContact(id: Long): FavoriteContact? = dao.getById(id)?.toDomain()

    override suspend fun saveContact(contact: FavoriteContact): Long {
        val entity = contact.toEntity()
        return if (contact.id == 0L) {
            val order = contact.order.takeIf { it >= 0 } ?: ((dao.maxOrder() ?: -1) + 1)
            dao.insert(entity.copy(sortOrder = order))
        } else {
            dao.update(entity)
            contact.id
        }
    }

    override suspend fun deleteContact(id: Long) = dao.delete(id)

    override suspend fun count(): Int = dao.count()

    override suspend fun reorder(contactIdsInOrder: List<Long>) {
        contactIdsInOrder.forEachIndexed { index, id -> dao.updateOrder(id, index) }
    }
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(private val dataStore: PreferencesDataStore) : PreferencesRepository {
    override fun getUserPreferences(): Flow<UserPreferences> = dataStore.preferencesFlow
    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) = dataStore.update(transform)
}

private fun FavoriteContactEntity.toDomain() = FavoriteContact(id, name, phone, photoColor, sortOrder)
private fun FavoriteContact.toEntity() = FavoriteContactEntity(id, name, phone, photoColor, order)
