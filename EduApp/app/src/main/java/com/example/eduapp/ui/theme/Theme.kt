package com.example.eduapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandIndigoLight,
    onPrimary = BrandIndigoDark,
    secondary = BrandAmber,
    onSecondary = BrandAmberDark,
    tertiary = BrandAmberDark,
    background = NeutralSurfaceDark,
    surface = NeutralSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = OnBrandIndigo,
    secondary = BrandAmberDark,
    onSecondary = OnBrandIndigo,
    tertiary = BrandAmber,
    background = NeutralSurfaceLight,
    surface = NeutralSurfaceLight
)

@Composable
fun EduAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic (wallpaper-derived) color is turned off by default so the in-app
    // palette always matches the app's own launcher icon branding, rather than
    // shifting with each user's system wallpaper.
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
