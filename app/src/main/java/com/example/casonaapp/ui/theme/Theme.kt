package com.example.casonaapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.casonaapp.viewmodels.ThemeViewModel

private val DarkColorScheme = darkColorScheme(
    primary = LightOlive,
    onPrimary = DarkGray,
    primaryContainer = DarkOlive,
    onPrimaryContainer = LightOlive,

    secondary = LightPurple,
    onSecondary = DarkGray,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = LightPurple,

    tertiary = PaleBlue,
    onTertiary = DarkGray,
    tertiaryContainer = DarkBlue,
    onTertiaryContainer = PaleBlue,

    background = DarkGray,
    onBackground = Cream,

    surface = Color(0xFF1C1B1A),
    onSurface = Cream,
    surfaceVariant = Color(0xFF353430),
    onSurfaceVariant = LightGray,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = MediumGray,
    outlineVariant = Color(0xFF504E49)
)

private val LightColorScheme = lightColorScheme(
    primary = Olive,
    onPrimary = Color.White,
    primaryContainer = LightOlive,
    onPrimaryContainer = DarkGray,

    secondary = PurpleGray,
    onSecondary = Color.White,
    secondaryContainer = LightPurple,
    onSecondaryContainer = DarkGray,

    tertiary = LightBlue,
    onTertiary = DarkGray,
    tertiaryContainer = PaleBlue,
    onTertiaryContainer = DarkGray,

    background = Cream,
    onBackground = DarkGray,

    surface = Color.White,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = MediumGray,
    outlineVariant = LightGray
)


@Composable
fun CasonaAppTheme(
    themeViewModel: ThemeViewModel? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val customDarkTheme = themeViewModel?.isDarkTheme?.collectAsState()?.value

    val useDarkTheme = when (customDarkTheme) {
        true -> true
        false -> false
        null -> darkTheme // Si es null, usar el tema del sistema
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val currentWindow = (view.context as? Activity)?.window
        currentWindow?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !useDarkTheme
                isAppearanceLightNavigationBars = !useDarkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}