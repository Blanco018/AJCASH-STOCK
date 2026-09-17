package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RevisionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {
    @Query("SELECT * FROM revision_records WHERE vehicleId = :vehicleId ORDER BY timestamp DESC")
    fun getRevisionsForVehicle(vehicleId: String): Flow<List<RevisionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(record: RevisionRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisions(records: List<RevisionRecord>)

    @Query("SELECT COUNT(*) FROM revision_records")
    suspend fun getCount(): Int
}
