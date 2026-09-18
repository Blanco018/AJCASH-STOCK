package com.example.data.remote

import com.example.data.model.StockItem
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirestoreStockItem(
    @DocumentId
    val id: String = "",
    val vehicleId: String = "",
    val category: String = "",
    val name: String = "",
    val currentQuantity: Int = 0,
    val minimumQuantity: Int = 0,
    val unit: String = "uds",
    val description: String = "",
    val lastUpdatedBy: String = "",
    val updatedAtTimestamp: Long = 0L
) {
    fun toStockItem(): StockItem {
        return StockItem(
            id = id,
            vehicleId = vehicleId,
            category = category,
            name = name,
            currentQuantity = currentQuantity,
            minimumQuantity = minimumQuantity,
            unit = unit.ifEmpty { "uds" },
            description = description
        )
    }

    companion object {
        fun fromStockItem(item: StockItem, updatedBy: String = ""): FirestoreStockItem {
            return FirestoreStockItem(
                id = item.id,
                vehicleId = item.vehicleId,
                category = item.category,
                name = item.name,
                currentQuantity = item.currentQuantity,
                minimumQuantity = item.minimumQuantity,
                unit = item.unit,
                description = item.description,
                lastUpdatedBy = updatedBy,
                updatedAtTimestamp = System.currentTimeMillis()
            )
        }
    }
}
