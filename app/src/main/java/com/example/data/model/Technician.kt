package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "technicians")
data class Technician(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val number: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = "$name · Nº $number"

    val badgeLabel: String
        get() = "$name (#$number)"
}
