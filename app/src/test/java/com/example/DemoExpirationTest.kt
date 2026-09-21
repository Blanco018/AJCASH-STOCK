package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.DemoExpirationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoExpirationTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("ajcash_demo_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun testDemoNotExpiredOnFirstLaunch() {
        val firstLaunch = DemoExpirationManager.getOrCreateFirstLaunchTime(context)
        assertTrue("El timestamp de primer inicio debe ser mayor a 0", firstLaunch > 0L)
        assertFalse("La demo no debe estar expirada al primer inicio", DemoExpirationManager.isDemoExpired(context))
        assertTrue("Deben restar milisegundos válidos", DemoExpirationManager.getRemainingMillis(context) > 0L)
    }

    @Test
    fun testSimulatedExpirationTriggersLock() {
        DemoExpirationManager.setSimulatedExpired(context, true)
        assertTrue("La demo debe marcarse como expirada cuando se simula", DemoExpirationManager.isDemoExpired(context))
        
        DemoExpirationManager.setSimulatedExpired(context, false)
        assertFalse("Al desactivar la simulación debe volver a estar activa", DemoExpirationManager.isDemoExpired(context))
    }

    @Test
    fun testFormatRemainingTime() {
        val millis23Hours = 23 * 3600 * 1000L + 45 * 60 * 1000L
        assertEquals("23h 45m", DemoExpirationManager.formatRemainingTime(millis23Hours))
        assertEquals("Expirada", DemoExpirationManager.formatRemainingTime(0L))
    }
}
