package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Technician
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians ORDER BY name ASC")
    fun getAllTechnicians(): Flow<List<Technician>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnician(technician: Technician)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnicians(technicians: List<Technician>)

    @Delete
    suspend fun deleteTechnician(technician: Technician)

    @Query("DELETE FROM technicians WHERE id = :id")
    suspend fun deleteTechnicianById(id: String)

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun getCount(): Int
}
