package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "revision_records")
data class RevisionRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val technicianName: String,
    val technicianNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val changesSummary: String // Delimited by newlines or JSON
) {
    val changesList: List<String>
        get() = if (changesSummary.isBlank()) {
            listOf("Sin cambios · Stock verificado conforme")
        } else {
            changesSummary.split("\n").filter { it.isNotBlank() }
        }
}
