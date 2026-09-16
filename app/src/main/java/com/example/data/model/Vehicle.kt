package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey
    val id: String,
    val name: String,
    val model: String = "",
    val type: String, // "Coche de empresa" or "Furgoneta de empresa"
    val plate: String,
    val imageDrawableName: String, // e.g. "vehicle_car_1"
    val lastRevisionTimestamp: Long = System.currentTimeMillis(),
    val lastReviewedBy: String = "Técnico de Guardia",
    val notes: String = ""
)
