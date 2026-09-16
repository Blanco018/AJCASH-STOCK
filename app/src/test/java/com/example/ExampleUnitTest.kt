package com.example

import com.example.data.model.StockItem
import com.example.data.model.Vehicle
import com.example.data.model.VehicleStockSummary
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun stockItem_withZeroMinimum_isNeverUnderMinimumWhenNonNegative() {
        val itemZeroCurrent = StockItem(
            id = "coche_1_tpv_205",
            vehicleId = "coche_1",
            name = "TPV Modelo x205",
            category = "TPVs",
            minimumQuantity = 0,
            currentQuantity = 0,
            unit = "uds"
        )
        assertFalse(itemZeroCurrent.isUnderMinimum)
        assertEquals(0, itemZeroCurrent.deficit)

        val itemPositiveCurrent = itemZeroCurrent.copy(currentQuantity = 2)
        assertFalse(itemPositiveCurrent.isUnderMinimum)
        assertEquals(0, itemPositiveCurrent.deficit)
    }

    @Test
    fun vehicleStockSummary_withZeroDeficits_hasFullCoverage() {
        val dummyVehicle = Vehicle(
            id = "coche_1",
            name = "Coche 1",
            model = "Opel Corsa",
            type = "Vehículo técnico",
            plate = "4582-KLL",
            imageDrawableName = "vehicle_car_1"
        )
        val summary = VehicleStockSummary(
            vehicle = dummyVehicle,
            totalItems = 14,
            underMinimumCount = 0,
            criticalDeficits = emptyList()
        )
        assertTrue(summary.isReadyForGuard)
        assertEquals(100, summary.coveragePercentage)
        val floatProgress = (summary.coveragePercentage.toFloat() / 100f).coerceIn(0f, 1f)
        assertEquals(1.0f, floatProgress, 0.001f)
    }
}
