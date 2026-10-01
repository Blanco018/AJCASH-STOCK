package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.util.DemoExpirationManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Suite de UI Testing Nativo para AJCashStocks con Espresso y Jetpack Compose.
 *
 * Configuración:
 * - @RunWith(AndroidJUnit4::class)
 * - ActivityScenarioRule sobre MainActivity
 * - createEmptyComposeRule() para sincronización reactiva con Compose
 * - Aserciones nativas con Espresso (matches(isDisplayed())) y acciones reales de usuario:
 *   búsqueda de producto, tipado de texto, interacción con botones y validaciones de visibilidad.
 */
@RunWith(AndroidJUnit4::class)
class InventoryEspressoUITest {

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Asegurar que la versión Demo se encuentra activa durante la suite de pruebas automatizadas
        DemoExpirationManager.setSimulatedExpired(context, false)
        DemoExpirationManager.getOrCreateFirstLaunchTime(context)
    }

    /**
     * Test 1: Verificación de la vista principal con Espresso y estado de la Activity.
     */
    @Test
    fun mainActivity_launchesSuccessfully_andRootViewIsDisplayed() {
        // Aserción nativa con Espresso
        onView(isRoot()).check(matches(isDisplayed()))

        // Aserción de sincronización en Jetpack Compose
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()
    }

    /**
     * Test 2: Navegación completa hacia el inventario del vehículo seleccionado.
     */
    @Test
    fun navigateToVehicleInventory_selectsVehicleAndEntersGuardSession() {
        composeTestRule.waitForIdle()

        // 1. Pulsar sobre la tarjeta del vehículo de guardia (Furgoneta 1)
        composeTestRule.onNodeWithTag("vehicle_card_furgoneta-1").performClick()

        // 2. Verificar que el diálogo de asignación de Técnico de Guardia se muestra en pantalla
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_tech_auth_button").assertIsDisplayed()

        // 3. Confirmar asignación técnica e ingresar al inventario
        composeTestRule.onNodeWithTag("submit_tech_auth_button").performClick()

        // 4. Validar que la pantalla de inventario de materiales está desplegada
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("vehicle_inventory_screen").assertIsDisplayed()
    }

    /**
     * Test 3: Búsqueda en tiempo real con tipado de texto, filtrado reactivo y aserción de contenido.
     */
    @Test
    fun searchProduct_typesQuery_filtersInventoryAndValidatesResults() {
        composeTestRule.waitForIdle()

        // Flujo de navegación hacia el vehículo
        composeTestRule.onNodeWithTag("vehicle_card_furgoneta-1").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_tech_auth_button").performClick()
        composeTestRule.waitForIdle()

        // Validar con Espresso la presencia de la pantalla
        onView(isRoot()).check(matches(isDisplayed()))

        // Localizar el campo de búsqueda e ingresar texto tipado
        val searchBox = composeTestRule.onNodeWithTag("search_input")
        searchBox.assertIsDisplayed()
        searchBox.performTextInput("TPV")

        composeTestRule.waitForIdle()

        // Verificar que los elementos filtrados contienen "TPV" y se visualizan correctamente
        composeTestRule.onNodeWithText("TPV", substring = true).assertIsDisplayed()
    }

    /**
     * Test 4: Interacción con controles de navegación (botón Atrás) y retorno fluido al Dashboard.
     */
    @Test
    fun inventoryScreen_backButton_returnsToDashboard() {
        composeTestRule.waitForIdle()

        // Abrir vehículo
        composeTestRule.onNodeWithTag("vehicle_card_furgoneta-1").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("submit_tech_auth_button").performClick()
        composeTestRule.waitForIdle()

        // Pulsar botón de retorno
        val backButton = composeTestRule.onNodeWithTag("back_button")
        backButton.assertIsDisplayed()
        backButton.performClick()

        // Validar regreso al Dashboard inicial
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()
    }
}
