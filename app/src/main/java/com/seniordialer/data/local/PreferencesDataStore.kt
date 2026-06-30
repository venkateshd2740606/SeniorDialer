package com.seniordialer.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.seniordialer.domain.model.AppTheme
import com.seniordialer.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("seniordialer_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val FONT_SCALE = floatPreferencesKey("font_scale")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            appTheme = runCatching { AppTheme.valueOf(prefs[Keys.APP_THEME] ?: AppTheme.SYSTEM.name) }
                .getOrDefault(AppTheme.SYSTEM),
            highContrastMode = prefs[Keys.HIGH_CONTRAST] ?: true,
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.4f
        )
    }

    suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { prefs ->
            val current = UserPreferences(
                appTheme = runCatching { AppTheme.valueOf(prefs[Keys.APP_THEME] ?: AppTheme.SYSTEM.name) }
                    .getOrDefault(AppTheme.SYSTEM),
                highContrastMode = prefs[Keys.HIGH_CONTRAST] ?: true,
                fontScale = prefs[Keys.FONT_SCALE] ?: 1.4f
            )
            val updated = transform(current)
            prefs[Keys.APP_THEME] = updated.appTheme.name
            prefs[Keys.HIGH_CONTRAST] = updated.highContrastMode
            prefs[Keys.FONT_SCALE] = updated.fontScale
        }
    }
}
