package com.seniordialer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seniordialer.domain.model.ContactPhotoColors
import com.seniordialer.domain.model.FavoriteContact
import com.seniordialer.domain.model.UserPreferences
import com.seniordialer.domain.repository.FavoriteContactRepository
import com.seniordialer.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeniorDialerViewModel @Inject constructor(
    private val contactRepo: FavoriteContactRepository,
    private val prefsRepo: PreferencesRepository
) : ViewModel() {

    val contacts: StateFlow<List<FavoriteContact>> = contactRepo.observeContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val preferences: StateFlow<UserPreferences> = prefsRepo.getUserPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _dialedNumber = MutableStateFlow("")
    val dialedNumber: StateFlow<String> = _dialedNumber

    fun appendDigit(digit: Char) {
        if (_dialedNumber.value.length < 15) _dialedNumber.value += digit
    }

    fun backspace() {
        if (_dialedNumber.value.isNotEmpty()) {
            _dialedNumber.value = _dialedNumber.value.dropLast(1)
        }
    }

    fun clearNumber() {
        _dialedNumber.value = ""
    }

    fun setDialedNumber(number: String) {
        _dialedNumber.value = number.filter { it.isDigit() || it == '+' }
    }

    fun saveContact(contact: FavoriteContact, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            if (contact.id == 0L) {
                val count = contactRepo.count()
                if (count >= 6) {
                    onResult(false, "Maximum 6 favorites allowed")
                    return@launch
                }
            }
            contactRepo.saveContact(contact)
            onResult(true, null)
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch { contactRepo.deleteContact(id) }
    }

    fun moveContactUp(id: Long) {
        viewModelScope.launch {
            val list = contacts.value
            val index = list.indexOfFirst { it.id == id }
            if (index <= 0) return@launch
            val reordered = list.toMutableList()
            val item = reordered.removeAt(index)
            reordered.add(index - 1, item)
            contactRepo.reorder(reordered.map { it.id })
        }
    }

    fun moveContactDown(id: Long) {
        viewModelScope.launch {
            val list = contacts.value
            val index = list.indexOfFirst { it.id == id }
            if (index < 0 || index >= list.lastIndex) return@launch
            val reordered = list.toMutableList()
            val item = reordered.removeAt(index)
            reordered.add(index + 1, item)
            contactRepo.reorder(reordered.map { it.id })
        }
    }

    fun nextPhotoColor(): Long {
        val used = contacts.value.map { it.photoColor }.toSet()
        return ContactPhotoColors.firstOrNull { it !in used } ?: ContactPhotoColors.random()
    }

    fun updateHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.updatePreferences { it.copy(highContrastMode = enabled) }
        }
    }
}
