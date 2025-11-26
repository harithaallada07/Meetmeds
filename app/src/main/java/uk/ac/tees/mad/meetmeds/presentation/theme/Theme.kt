package uk.ac.tees.mad.meetmeds.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define the Dark Color Scheme using your custom colors
private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = OnTealPrimaryDark,
    secondary = TealSecondaryDark,
    onSecondary = OnTealSecondaryDark,
    tertiary = TealTertiaryDark,
    onTertiary = OnTealTertiaryDark,
    background = TealBackgroundDark,
    onBackground = OnTealBackgroundDark,
    surface = TealSurfaceDark,
    onSurface = OnTealSurfaceDark
)

// Define the Light Color Scheme using your custom colors
private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = OnTealPrimaryLight,
    secondary = TealSecondaryLight,
    onSecondary = OnTealSecondaryLight,
    tertiary = TealTertiaryLight,
    onTertiary = OnTealTertiaryLight,
    background = TealBackgroundLight,
    onBackground = OnTealBackgroundLight,
    surface = TealSurfaceLight,
    onSurface = OnTealSurfaceLight
)

@Composable
fun MeetMedsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // FIX: Set this to FALSE to force your Teal colors instead of Wallpaper colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Optional: Fix Status Bar color logic
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Ensure you have a Typography.kt file, or remove this line if using default
        content = content
    )
}