package com.seniordialer.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.seniordialer.domain.model.AppTheme

private const val SENIOR_FONT_SCALE = 1.4f

@Composable
fun SeniorDialerTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    highContrast: Boolean = true,
    fontScale: Float = SENIOR_FONT_SCALE,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val dark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> systemDark
    }

    val colorScheme = if (dark || highContrast) {
        darkColorScheme(
            primary = Color(0xFFFFD600),
            onPrimary = Color.Black,
            secondary = Color(0xFFFFFFFF),
            onSecondary = Color.Black,
            background = Color.Black,
            surface = Color(0xFF1A1A1A),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF000000),
            onPrimary = Color.White,
            background = Color.White,
            surface = Color(0xFFF5F5F5),
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }

    val base = androidx.compose.material3.Typography()
    val scaledTypography = androidx.compose.material3.Typography(
        displayLarge = base.displayLarge.copy(fontSize = (57 * fontScale).sp),
        headlineLarge = base.headlineLarge.copy(fontSize = (32 * fontScale).sp),
        headlineMedium = base.headlineMedium.copy(fontSize = (28 * fontScale).sp),
        titleLarge = base.titleLarge.copy(fontSize = (22 * fontScale).sp),
        titleMedium = base.titleMedium.copy(fontSize = (18 * fontScale).sp),
        bodyLarge = base.bodyLarge.copy(fontSize = (18 * fontScale).sp),
        bodyMedium = base.bodyMedium.copy(fontSize = (16 * fontScale).sp),
        labelLarge = base.labelLarge.copy(fontSize = (16 * fontScale).sp)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark && !highContrast
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = scaledTypography, content = content)
}
