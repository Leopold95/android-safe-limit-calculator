package com.alexandr.safelimitcalculator.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.alexandr.safelimitcalculator.ui.theme.TypographyTokens

val LocalColorTokens = staticCompositionLocalOf<ColorTokens> {
    error("No ColorTokens provided")
}

val LocalTypographyTokens = staticCompositionLocalOf<TypographyTokens> {
    error("No TypographyTokens provided")
}

val LocalShapeTokens = staticCompositionLocalOf<ShapeTokens> {
    error("No ShapeTokens provided")
}

val LocalDimensionTokens = staticCompositionLocalOf<DimensionTokens> {
    error("No DimensionTokens provided")
}

object LocalAppTheme {
    val colors: ColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalColorTokens.current

    val typography: TypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalTypographyTokens.current

    val shapes: ShapeTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalShapeTokens.current

    val dimen: DimensionTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensionTokens.current
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val colorTokens = ColorTokens(
        primary = colorScheme.primary,
        onPrimary = colorScheme.onPrimary,
        primaryContainer = colorScheme.primaryContainer,
        onPrimaryContainer = colorScheme.onPrimaryContainer,
        secondary = colorScheme.secondary,
        onSecondary = colorScheme.onSecondary,
        secondaryContainer = colorScheme.secondaryContainer,
        onSecondaryContainer = colorScheme.onSecondaryContainer,
        tertiary = colorScheme.tertiary,
        onTertiary = colorScheme.onTertiary,
        tertiaryContainer = colorScheme.tertiaryContainer,
        onTertiaryContainer = colorScheme.onTertiaryContainer,
        error = colorScheme.error,
        onError = colorScheme.onError,
        errorContainer = colorScheme.errorContainer,
        onErrorContainer = colorScheme.onErrorContainer,
        background = colorScheme.background,
        onBackground = colorScheme.onBackground,
        surface = colorScheme.surface,
        onSurface = colorScheme.onSurface,
        surfaceVariant = colorScheme.surfaceVariant,
        onSurfaceVariant = colorScheme.onSurfaceVariant,
        outline = colorScheme.outline,
        inverseOnSurface = colorScheme.inverseOnSurface,
        inverseSurface = colorScheme.inverseSurface,
        inversePrimary = colorScheme.inversePrimary,
        surfaceTint = colorScheme.surfaceTint,
        outlineVariant = colorScheme.outlineVariant,
        scrim = colorScheme.scrim,
    )

    val typographyTokens = TypographyTokens(
        displayLarge = MaterialTheme.typography.displayLarge,
        displayMedium = MaterialTheme.typography.displayMedium,
        displaySmall = MaterialTheme.typography.displaySmall,
        headlineLarge = MaterialTheme.typography.headlineLarge,
        headlineMedium = MaterialTheme.typography.headlineMedium,
        headlineSmall = MaterialTheme.typography.headlineSmall,
        titleLarge = MaterialTheme.typography.titleLarge,
        titleMedium = MaterialTheme.typography.titleMedium,
        titleSmall = MaterialTheme.typography.titleSmall,
        bodyLarge = MaterialTheme.typography.bodyLarge,
        bodyMedium = MaterialTheme.typography.bodyMedium,
        bodySmall = MaterialTheme.typography.bodySmall,
        labelLarge = MaterialTheme.typography.labelLarge,
        labelMedium = MaterialTheme.typography.labelMedium,
        labelSmall = MaterialTheme.typography.labelSmall,
    )

    val shapeTokens = ShapeTokens(
        extraSmall = MaterialTheme.shapes.extraSmall,
        small = MaterialTheme.shapes.small,
        medium = MaterialTheme.shapes.medium,
        large = MaterialTheme.shapes.large,
        extraLarge = MaterialTheme.shapes.extraLarge,
    )

    val dimensionTokens = DimensionTokens()

    CompositionLocalProvider(
        LocalColorTokens provides colorTokens,
        LocalTypographyTokens provides typographyTokens,
        LocalShapeTokens provides shapeTokens,
        LocalDimensionTokens provides dimensionTokens,
        content = content
    )
}
