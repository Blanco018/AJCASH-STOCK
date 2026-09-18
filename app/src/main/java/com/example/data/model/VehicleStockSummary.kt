package com.example.data.model

data class DeficientItemSummary(
    val name: String,
    val currentQuantity: Int,
    val minimumQuantity: Int,
    val missingQuantity: Int,
    val unit: String
)

data class VehicleStockSummary(
    val vehicle: Vehicle,
    val totalItems: Int,
    val underMinimumCount: Int,
    val criticalDeficits: List<String>,
    val deficientItems: List<DeficientItemSummary> = emptyList()
) {
    val isReadyForGuard: Boolean
        get() = underMinimumCount == 0

    val coveragePercentage: Int
        get() = if (totalItems == 0) 100 else (((totalItems - underMinimumCount).toFloat() / totalItems) * 100).toInt()
}

