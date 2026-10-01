package com.example.util

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Administrador del periodo de prueba Demo (24 Horas) para AJCASH-STOCK.
 *
 * Registra la marca de tiempo de la primera ejecución y calcula si han transcurrido
 * 24 horas continuas desde dicho instante. También contempla una fecha límite estática
 * configurable para pruebas o cierres absolutos de demo.
 */
object DemoExpirationManager {
    private const val PREFS_NAME = "ajcash_demo_prefs"
    private const val KEY_FIRST_LAUNCH = "demo_first_launch_timestamp"
    private const val KEY_SIMULATE_EXPIRED = "demo_simulate_expired_test"

    const val DEMO_DURATION_HOURS = 24L
    const val DEMO_DURATION_MILLIS = DEMO_DURATION_HOURS * 60 * 60 * 1000L

    // Fecha límite estática de referencia para prueba absoluta (por defecto en fecha futura para respetar las 24 horas dinámicas)
    const val STATIC_EXPIRE_DATE_STRING = "2030-01-01 00:00:00"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Obtiene o inicializa la marca de tiempo del primer inicio de la app.
     */
    fun getOrCreateFirstLaunchTime(context: Context): Long {
        val prefs = getPrefs(context)
        var firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH, 0L)
        if (firstLaunch <= 0L) {
            firstLaunch = System.currentTimeMillis()
            prefs.edit().putLong(KEY_FIRST_LAUNCH, firstLaunch).apply()
        }
        return firstLaunch
    }

    /**
     * Calcula el instante de expiración en milisegundos (24h tras el primer arranque).
     */
    fun getExpirationTimestamp(context: Context): Long {
        val firstLaunch = getOrCreateFirstLaunchTime(context)
        return firstLaunch + DEMO_DURATION_MILLIS
    }

    /**
     * Determina si la versión demo ha caducado.
     */
    fun isDemoExpired(context: Context): Boolean {
        val prefs = getPrefs(context)
        if (prefs.getBoolean(KEY_SIMULATE_EXPIRED, false)) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val expirationTime = getExpirationTimestamp(context)

        val staticExpirationTime = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.parse(STATIC_EXPIRE_DATE_STRING)?.time ?: Long.MAX_VALUE
        } catch (_: Exception) {
            Long.MAX_VALUE
        }

        return currentTime >= expirationTime || currentTime >= staticExpirationTime
    }

    /**
     * Devuelve el tiempo restante en milisegundos (mínimo 0).
     */
    fun getRemainingMillis(context: Context): Long {
        val remaining = getExpirationTimestamp(context) - System.currentTimeMillis()
        return if (remaining > 0L) remaining else 0L
    }

    /**
     * Formatea el tiempo restante en texto legible (ej: "23h 45m" o "Expirada").
     */
    fun formatRemainingTime(remainingMillis: Long): String {
        if (remainingMillis <= 0L) return "Expirada"
        val totalMinutes = remainingMillis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}h ${minutes}m"
    }

    /**
     * Formatea la fecha exacta en la que finaliza el periodo de prueba.
     */
    fun formatExpirationDate(context: Context): String {
        val expTime = getExpirationTimestamp(context)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(expTime))
    }

    /**
     * Permite alternar la simulación de expiración para validar la pantalla de bloqueo en pruebas.
     */
    fun setSimulatedExpired(context: Context, expired: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SIMULATE_EXPIRED, expired).apply()
    }

    fun isSimulatedExpired(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SIMULATE_EXPIRED, false)
    }
}
