package com.example.data.model

data class VehicleStockSummary(
    val vehicle: Vehicle,
    val totalItems: Int,
    val underMinimumCount: Int,
    val criticalDeficits: List<String>
) {
    val isReadyForGuard: Boolean
        get() = underMinimumCount == 0

    val coveragePercentage: Int
        get() = if (totalItems == 0) 100 else (((totalItems - underMinimumCount).toFloat() / totalItems) * 100).toInt()
}
