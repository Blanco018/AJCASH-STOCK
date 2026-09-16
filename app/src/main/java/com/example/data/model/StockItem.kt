package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_items",
    indices = [
        Index(value = ["vehicleId", "name"], unique = true),
        Index(value = ["vehicleId", "category"])
    ]
)
data class StockItem(
    @PrimaryKey
    val id: String,
    val vehicleId: String,
    val category: String, // "TPVs", "Impresoras", "Cables", "Consumibles", "Periféricos", "Red"
    val name: String,
    val currentQuantity: Int,
    val minimumQuantity: Int,
    val unit: String = "uds",
    val description: String = ""
) {
    val isUnderMinimum: Boolean
        get() = currentQuantity < minimumQuantity

    val deficit: Int
        get() = (minimumQuantity - currentQuantity).coerceAtLeast(0)
}

