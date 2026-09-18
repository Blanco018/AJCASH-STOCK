package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Paleta corporativa permanente en Modo Oscuro (Dark Theme) para AJCASH-STOCK.
 *
 * NOTA DE DISEÑO Y CONTRASTE:
 * Se fuerza permanentemente el modo oscuro por requerimientos estrictos de identidad visual
 * y ergonomía en intervenciones técnicas y guardias. Se emplean fondos oscuros (#121212 / #1E1E1E),
 * acentos en verde corporativo (#4CAF50) y tipografías en blanco puro (#FFFFFF) para asegurar
 * máxima legibilidad y evitar inconsistencias de contraste cuando el sistema operativo del usuario
 * tiene activado el Modo Claro o temas dinámicos.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),              // Verde corporativo principal
    onPrimary = Color(0xFFFFFFFF),            // Texto sobre verde
    primaryContainer = Color(0xFF1E3A1E),     // Contenedor verde oscuro
    onPrimaryContainer = Color(0xFF86EFAC),   // Texto/icono sobre contenedor
    secondary = Color(0xFF86EFAC),            // Verde menta de soporte
    onSecondary = Color(0xFF0F2E14),
    secondaryContainer = Color(0xFF1B3820),
    onSecondaryContainer = Color(0xFFC7F9D4),
    tertiary = Color(0xFF38BDF8),             // Azul cian para acciones técnicas
    onTertiary = Color(0xFF082F49),
    background = Color(0xFF121212),           // Fondo base de la aplicación (#121212)
    onBackground = Color(0xFFFFFFFF),         // Texto principal (#FFFFFF)
    surface = Color(0xFF1E1E1E),              // Superficie de tarjetas y modales (#1E1E1E)
    onSurface = Color(0xFFFFFFFF),            // Texto sobre tarjetas (#FFFFFF)
    surfaceVariant = Color(0xFF262626),       // Superficie de tarjetas anidadas
    onSurfaceVariant = Color(0xFFE2E8F0),     // Texto secundario legible
    outline = Color(0xFF383838),              // Bordes divisores sutiles
    error = Color(0xFFEF4444),                // Estado de déficit / alerta
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)

/**
 * Tema principal de AJCASH-STOCK.
 *
 * Por especificación de diseño corporativo:
 * 1. `darkTheme` está establecido a `true` de forma permanente para ignorar la configuración
 *    global del sistema operativo y mantener siempre la estética oscura de alto contraste.
 * 2. `dynamicColor` está desactivado (`false`) para impedir que Material You sobrescriba
 *    la identidad de marca con la paleta del fondo de pantalla del teléfono.
 *
 * @param darkTheme Determina si se aplica el tema oscuro. Fijado en `true` por defecto.
 * @param dynamicColor Determina si se usa Dynamic Color en Android 12+. Fijado en `false`.
 * @param content Contenido Compose a renderizar bajo este tema.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Se fuerza permanentemente la paleta DarkColorScheme definida para AJCASH-STOCK
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


