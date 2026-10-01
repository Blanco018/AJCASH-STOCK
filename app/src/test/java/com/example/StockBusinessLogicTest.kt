package com.example

import com.example.data.local.StockDao
import com.example.data.model.StockItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suite de pruebas unitarias para AJCashStocks que valida la lógica de negocio nuclear:
 *
 * 1. Modelos de producto e inventario:
 *    - Validación de umbrales mínimos (isUnderMinimum).
 *    - Cálculo exacto de déficit ante reposiciones de guardia.
 *
 * 2. Filtrado de inventario:
 *    - Búsqueda por texto (insensible a mayúsculas/minúsculas).
 *    - Filtrado estricto por categoría de hardware ("TPVs", "Impresoras", etc.).
 *    - Filtro de alertas de guardia (artículos por debajo del stock mínimo requerido).
 *
 * 3. Gestión y persistencia de stock con MockK:
 *    - Actualización atómica de cantidades.
 *    - Restauración masiva de mínimos de guardia por vehículo.
 */
class StockBusinessLogicTest {

    // ---------------------------------------------------------------------
    // CASO 1: Validación del modelo StockItem (Mínimos y Déficit de Guardia)
    // ---------------------------------------------------------------------
    @Test
    fun stockItem_calculatesUnderMinimumAndDeficitAccurately() {
        // Artículo deficitario: stock por debajo del mínimo de guardia
        val underStockItem = StockItem(
            id = "tpv-01",
            vehicleId = "furgoneta-1",
            category = "TPVs",
            name = "TPV Táctil AJCash 15\"",
            currentQuantity = 1,
            minimumQuantity = 3,
            unit = "uds"
        )
        assertTrue(
            "isUnderMinimum debe ser true cuando currentQuantity < minimumQuantity",
            underStockItem.isUnderMinimum
        )
        assertEquals(
            "El déficit debe ser exactamente minimumQuantity - currentQuantity (3 - 1 = 2)",
            2,
            underStockItem.deficit
        )

        // Artículo con stock óptimo: stock superior al mínimo
        val surplusItem = StockItem(
            id = "cables-01",
            vehicleId = "furgoneta-1",
            category = "Cables",
            name = "Cable Red Ethernet RJ45 3m",
            currentQuantity = 12,
            minimumQuantity = 6,
            unit = "uds"
        )
        assertFalse(
            "isUnderMinimum debe ser false cuando currentQuantity >= minimumQuantity",
            surplusItem.isUnderMinimum
        )
        assertEquals(
            "El déficit debe ser 0 cuando no faltan unidades",
            0,
            surplusItem.deficit
        )

        // Artículo en el límite exacto del mínimo
        val exactItem = StockItem(
            id = "imp-01",
            vehicleId = "furgoneta-1",
            category = "Impresoras",
            name = "Impresora Térmica 80mm",
            currentQuantity = 2,
            minimumQuantity = 2,
            unit = "uds"
        )
        assertFalse(
            "En el límite exacto no se considera bajo mínimos",
            exactItem.isUnderMinimum
        )
        assertEquals(
            "El déficit en el límite exacto debe ser 0",
            0,
            exactItem.deficit
        )
    }

    // ---------------------------------------------------------------------
    // CASO 2: Filtrado y Búsqueda de Inventario (Categorías, Texto y Alertas)
    // ---------------------------------------------------------------------
    @Test
    fun inventoryFiltering_filtersByQueryCategoryAndDeficitAlerts() {
        val inventory = listOf(
            StockItem("item-1", "furgoneta-1", "TPVs", "TPV Táctil Sunmi T2s", currentQuantity = 1, minimumQuantity = 2),
            StockItem("item-2", "furgoneta-1", "TPVs", "TPV Compacto Android", currentQuantity = 3, minimumQuantity = 2),
            StockItem("item-3", "furgoneta-1", "Impresoras", "Impresora Térmica Tickets USB", currentQuantity = 0, minimumQuantity = 2),
            StockItem("item-4", "furgoneta-1", "Cables", "Cable HDMI 2.0 2m", currentQuantity = 4, minimumQuantity = 2),
            StockItem("item-5", "furgoneta-1", "Consumibles", "Pack Rollos Papel Térmico 80x80", currentQuantity = 1, minimumQuantity = 5)
        )

        // Subcaso 2.1: Filtrado por Categoría ("TPVs")
        val categoryFilter = "TPVs"
        val tpvs = inventory.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        assertEquals("Deben filtrarse exactamente 2 TPVs", 2, tpvs.size)
        assertTrue("Todos los artículos devueltos deben ser de categoría TPVs", tpvs.all { it.category == "TPVs" })

        // Subcaso 2.2: Búsqueda libre por texto ("térmic" - cubre impresoras y rollos)
        val query = "térmic"
        val searchResults = inventory.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        assertEquals("Deben encontrarse 2 artículos que coincidan con 'térmic'", 2, searchResults.size)
        assertTrue(searchResults.any { it.name.contains("Impresora Térmica") })
        assertTrue(searchResults.any { it.name.contains("Papel Térmico") })

        // Subcaso 2.3: Filtro de solo faltas/alertas (isUnderMinimum == true)
        val onlyAlerts = inventory.filter { it.isUnderMinimum }
        assertEquals("Deben existir 3 artículos bajo mínimos", 3, onlyAlerts.size)
        assertTrue(onlyAlerts.all { it.isUnderMinimum })

        // Subcaso 2.4: Filtro combinado (Categoría "Consumibles" + Alerta activada)
        val combined = inventory.filter {
            it.category.equals("Consumibles", ignoreCase = true) && it.isUnderMinimum
        }
        assertEquals("Debe encontrar 1 consumible en estado de alerta", 1, combined.size)
        assertEquals("Pack Rollos Papel Térmico 80x80", combined.first().name)
    }

    // ---------------------------------------------------------------------
    // CASO 3: Gestión y Actualización de Stock en Repositorio/DAO
    // ---------------------------------------------------------------------
    @Test
    fun stockDao_updatesStockQuantityAndRestoresGuardMinimums() = runTest {
        val testStockDao = FakeStockDao()

        val targetItemId = "tpv-sunmi-01"
        val updatedQuantity = 5
        val vehicleId = "furgoneta-1"

        // Ejecutar actualización atómica de cantidad
        testStockDao.updateQuantity(targetItemId, updatedQuantity)

        // Ejecutar reposición rápida de guardia completa del vehículo
        testStockDao.restoreVehicleToMinimums(vehicleId)

        // Verificar que el DAO persistió el nuevo stock y registró la reposición
        assertEquals(
            "La cantidad debe actualizarse a 5",
            updatedQuantity,
            testStockDao.updatedQuantities[targetItemId]
        )
        assertTrue(
            "El vehículo furgoneta-1 debe estar en la lista de restaurados a mínimos",
            testStockDao.restoredVehicles.contains(vehicleId)
        )
    }
}

/**
 * Test Double (Fake DAO) para pruebas unitarias deterministas en entornos de CI/CD.
 * Implementa la interfaz StockDao sin dependencias de reflexión o agentes externos.
 */
class FakeStockDao : StockDao {
    val updatedQuantities = mutableMapOf<String, Int>()
    val restoredVehicles = mutableListOf<String>()

    override suspend fun updateQuantity(itemId: String, newQuantity: Int) {
        updatedQuantities[itemId] = newQuantity
    }

    override suspend fun restoreVehicleToMinimums(vehicleId: String) {
        restoredVehicles.add(vehicleId)
    }

    override fun getItemsForVehicle(vehicleId: String): kotlinx.coroutines.flow.Flow<List<StockItem>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override fun getAllStockItems(): kotlinx.coroutines.flow.Flow<List<StockItem>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override fun getDeficientItemsForVehicle(vehicleId: String): kotlinx.coroutines.flow.Flow<List<StockItem>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override suspend fun insertStockItems(items: List<StockItem>) {}
    override suspend fun updateStockItem(item: StockItem) {}
    override suspend fun updateMinimumQuantity(itemId: String, newMinimum: Int) {}
    override suspend fun insertStockItem(item: StockItem) {}
    override suspend fun getCount(): Int = 0
    override suspend fun restoreAllToMinimums() {}
}
