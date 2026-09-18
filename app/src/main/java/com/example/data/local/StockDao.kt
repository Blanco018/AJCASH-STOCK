package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StockItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM stock_items WHERE vehicleId = :vehicleId ORDER BY category ASC, name ASC")
    fun getItemsForVehicle(vehicleId: String): Flow<List<StockItem>>

    @Query("SELECT * FROM stock_items")
    fun getAllStockItems(): Flow<List<StockItem>>

    @Query("SELECT * FROM stock_items WHERE vehicleId = :vehicleId AND currentQuantity < minimumQuantity")
    fun getDeficientItemsForVehicle(vehicleId: String): Flow<List<StockItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItems(items: List<StockItem>)

    @Update
    suspend fun updateStockItem(item: StockItem)

    @Query("UPDATE stock_items SET currentQuantity = :newQuantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: String, newQuantity: Int)

    @Query("UPDATE stock_items SET minimumQuantity = :newMinimum WHERE id = :itemId")
    suspend fun updateMinimumQuantity(itemId: String, newMinimum: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItem(item: StockItem)

    @Query("UPDATE stock_items SET currentQuantity = minimumQuantity WHERE vehicleId = :vehicleId AND currentQuantity < minimumQuantity")
    suspend fun restoreVehicleToMinimums(vehicleId: String)

    @Query("SELECT COUNT(*) FROM stock_items")
    suspend fun getCount(): Int

    @Query("UPDATE stock_items SET currentQuantity = minimumQuantity WHERE currentQuantity < minimumQuantity")
    suspend fun restoreAllToMinimums()
}

