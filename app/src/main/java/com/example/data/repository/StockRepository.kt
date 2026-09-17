package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.RevisionDao
import com.example.data.local.StockDao
import com.example.data.local.VehicleDao
import com.example.data.model.RevisionRecord
import com.example.data.model.StockItem
import com.example.data.model.Vehicle
import com.example.data.model.VehicleStockSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class StockRepository(
    private val database: AppDatabase,
    private val vehicleDao: VehicleDao,
    private val stockDao: StockDao,
    private val revisionDao: RevisionDao = database.revisionDao()
) {
    val vehiclesWithSummaries: Flow<List<VehicleStockSummary>> =
        combine(
            vehicleDao.getAllVehicles(),
            stockDao.getAllStockItems()
        ) { vehicles, allItems ->
            vehicles.map { vehicle ->
                val vehicleItems = allItems.filter { it.vehicleId == vehicle.id }
                val underMinimumItems = vehicleItems.filter { it.isUnderMinimum }
                VehicleStockSummary(
                    vehicle = vehicle,
                    totalItems = vehicleItems.size,
                    underMinimumCount = underMinimumItems.size,
                    criticalDeficits = underMinimumItems.map { "${it.name} (${it.currentQuantity}/${it.minimumQuantity} ${it.unit})" }
                )
            }
        }

    fun getVehicle(vehicleId: String): Flow<Vehicle?> {
        return vehicleDao.getVehicleById(vehicleId)
    }

    fun getItemsForVehicle(vehicleId: String): Flow<List<StockItem>> {
        return stockDao.getItemsForVehicle(vehicleId)
    }

    suspend fun updateQuantity(itemId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        val safeQty = newQuantity.coerceAtLeast(0)
        stockDao.updateQuantity(itemId, safeQty)
    }

    suspend fun updateMinimumQuantity(itemId: String, newMinimum: Int) = withContext(Dispatchers.IO) {
        val safeMin = newMinimum.coerceAtLeast(0)
        stockDao.updateMinimumQuantity(itemId, safeMin)
    }

    suspend fun addNewItemToVehicle(
        vehicleId: String,
        name: String,
        category: String,
        minimumQuantity: Int,
        initialQuantity: Int,
        unit: String = "uds"
    ) = withContext(Dispatchers.IO) {
        val deterministicId = "${vehicleId}_${category.lowercase()}_${name.lowercase().replace(" ", "_").replace(",", "_").replace("+", "_")}"
        val item = StockItem(
            id = deterministicId,
            vehicleId = vehicleId,
            category = category.trim(),
            name = name.trim(),
            currentQuantity = initialQuantity.coerceAtLeast(0),
            minimumQuantity = minimumQuantity.coerceAtLeast(0),
            unit = unit.trim().ifEmpty { "uds" }
        )
        stockDao.insertStockItem(item)
    }

    suspend fun updateStockItem(item: StockItem) = withContext(Dispatchers.IO) {
        stockDao.updateStockItem(item)
    }

    suspend fun restoreVehicleToMinimums(vehicleId: String, reviewerName: String = "Reposición de Mínimos") = withContext(Dispatchers.IO) {
        stockDao.restoreVehicleToMinimums(vehicleId)
        vehicleDao.updateRevision(vehicleId, System.currentTimeMillis(), reviewerName)
    }

    suspend fun confirmRevision(vehicleId: String, reviewerName: String) = withContext(Dispatchers.IO) {
        vehicleDao.updateRevision(vehicleId, System.currentTimeMillis(), reviewerName)
    }

    fun getRevisionsForVehicle(vehicleId: String): Flow<List<RevisionRecord>> {
        return revisionDao.getRevisionsForVehicle(vehicleId)
    }

    suspend fun recordRevision(
        vehicleId: String,
        technicianName: String,
        technicianNumber: String,
        changes: List<String>
    ) = withContext(Dispatchers.IO) {
        val summary = if (changes.isEmpty()) {
            "Sin cambios · Stock verificado conforme"
        } else {
            changes.joinToString("\n")
        }
        val record = RevisionRecord(
            vehicleId = vehicleId,
            technicianName = technicianName.trim().uppercase(),
            technicianNumber = technicianNumber.trim(),
            timestamp = System.currentTimeMillis(),
            changesSummary = summary
        )
        revisionDao.insertRevision(record)
        vehicleDao.updateRevision(vehicleId, System.currentTimeMillis(), "${record.technicianName} (#${record.technicianNumber})")
    }

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        if (vehicleDao.getCount() == 0 || stockDao.getCount() == 0) {
            AppDatabase.seedDatabase(database)
        } else {
            // Ensure vehicle models and plates are updated to the official values
            vehicleDao.insertVehicles(AppDatabase.getDefaultVehicles())

            // Ensure every vehicle has Cajón portamonedas 41x41 and the exact defaults
            val vehicles = AppDatabase.getDefaultVehicles()
            for (vehicle in vehicles) {
                val isVan = vehicle.type.contains("Furgoneta", ignoreCase = true)
                val drawerId = "${vehicle.id}_periféricos_cajón_portamonedas_41x41"
                addNewItemToVehicle(
                    vehicleId = vehicle.id,
                    name = "Cajón portamonedas 41x41",
                    category = "Periféricos",
                    minimumQuantity = if (isVan) 1 else 0,
                    initialQuantity = if (isVan) 1 else 0,
                    unit = "uds"
                )
            }

            if (revisionDao.getCount() == 0) {
                AppDatabase.seedDatabase(database)
            }
        }
    }
}
