package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY id ASC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId LIMIT 1")
    fun getVehicleById(vehicleId: String): Flow<Vehicle?>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId LIMIT 1")
    suspend fun getVehicleByIdOnce(vehicleId: String): Vehicle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<Vehicle>)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Query("UPDATE vehicles SET lastRevisionTimestamp = :timestamp, lastReviewedBy = :reviewer WHERE id = :vehicleId")
    suspend fun updateRevision(vehicleId: String, timestamp: Long, reviewer: String)

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getCount(): Int

    @Query("UPDATE vehicles SET lastRevisionTimestamp = NULL, lastReviewedBy = ''")
    suspend fun resetAllRevisionsMetadata()
}

