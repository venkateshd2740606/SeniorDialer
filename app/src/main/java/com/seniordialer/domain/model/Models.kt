package com.seniordialer.domain.model

enum class AppTheme(val displayName: String) {
    SYSTEM("System"), LIGHT("Light"), DARK("Dark")
}

data class UserPreferences(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val highContrastMode: Boolean = true,
    val fontScale: Float = 1.4f
)

data class FavoriteContact(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val photoColor: Long,
    val order: Int
)

val ContactPhotoColors = listOf(
    0xFF1565C0L, 0xFF2E7D32L, 0xFF6A1B9AL, 0xFFEF6C00L, 0xFFC62828L, 0xFF00838FL
)
