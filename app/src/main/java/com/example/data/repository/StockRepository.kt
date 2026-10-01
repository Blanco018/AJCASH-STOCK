package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.RevisionDao
import com.example.data.local.StockDao
import com.example.data.local.TechnicianDao
import com.example.data.local.VehicleDao
import com.example.data.model.DeficientItemSummary
import com.example.data.model.RevisionRecord
import com.example.data.model.StockItem
import com.example.data.model.Technician
import com.example.data.model.Vehicle
import com.example.data.model.VehicleStockSummary
import com.example.data.remote.CloudSyncState
import com.example.data.remote.FirestoreSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockRepository(
    private val database: AppDatabase,
    private val vehicleDao: VehicleDao,
    private val stockDao: StockDao,
    private val revisionDao: RevisionDao = database.revisionDao(),
    private val technicianDao: TechnicianDao = database.technicianDao(),
    private val firestoreService: FirestoreSyncService? = null
) {
    private val _cloudSyncState = MutableStateFlow(
        if (firestoreService?.isAvailable == true) CloudSyncState.ONLINE_SYNCED else CloudSyncState.OFFLINE_LOCAL
    )
    val cloudSyncState: Flow<CloudSyncState> = _cloudSyncState.asStateFlow()

    val allTechnicians: Flow<List<Technician>> = technicianDao.getAllTechnicians()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (technicianDao.getCount() == 0) {
                    technicianDao.insertTechnician(
                        Technician(
                            id = "tech_pablo_blanco",
                            name = "PABLO BLANCO",
                            number = "16"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("StockRepository", "Error ensuring default technician", e)
            }
        }
    }

    val vehiclesWithSummaries: Flow<List<VehicleStockSummary>> =
        combine(
            vehicleDao.getAllVehicles(),
            stockDao.getAllStockItems()
        ) { vehicles, allItems ->
            vehicles.map { vehicle ->
                val vehicleItems = allItems.filter { it.vehicleId == vehicle.id }
                val underMinimumItems = vehicleItems.filter { it.isUnderMinimum }
                val deficientSummaries = underMinimumItems.map {
                    DeficientItemSummary(
                        name = it.name,
                        currentQuantity = it.currentQuantity,
                        minimumQuantity = it.minimumQuantity,
                        missingQuantity = (it.minimumQuantity - it.currentQuantity).coerceAtLeast(0),
                        unit = it.unit
                    )
                }
                VehicleStockSummary(
                    vehicle = vehicle,
                    totalItems = vehicleItems.size,
                    underMinimumCount = underMinimumItems.size,
                    criticalDeficits = underMinimumItems.map { "${it.name} (${it.currentQuantity}/${it.minimumQuantity} ${it.unit})" },
                    deficientItems = deficientSummaries
                )
            }
        }

    fun getVehicle(vehicleId: String): Flow<Vehicle?> {
        return vehicleDao.getVehicleById(vehicleId)
    }

    /**
     * Devuelve el inventario reactivo del vehículo como Flow.
     * Al estar respaldado por Room como Single Source of Truth (SSOT), cualquier
     * cambio en Firestore que actualice la base de datos local se refleja al instante.
     */
    fun getItemsForVehicle(vehicleId: String): Flow<List<StockItem>> {
        return stockDao.getItemsForVehicle(vehicleId)
    }

    /**
     * Inicia la escucha en tiempo real desde Firestore.
     * Si otro técnico altera el stock en su dispositivo, Firestore emite el cambio
     * y se actualiza Room, provocando la recomposición instantánea de Compose.
     */
    fun startRealtimeCloudSync(scope: CoroutineScope) {
        val service = firestoreService ?: return
        if (!service.isAvailable) {
            _cloudSyncState.value = CloudSyncState.OFFLINE_LOCAL
            return
        }

        scope.launch(Dispatchers.IO) {
            try {
                service.observeRemoteInventory().collect { remoteItems ->
                    if (remoteItems.isNotEmpty()) {
                        val stockItems = remoteItems.map { it.toStockItem() }
                        stockDao.insertStockItems(stockItems)
                        _cloudSyncState.value = CloudSyncState.ONLINE_SYNCED
                    }
                }
            } catch (e: Exception) {
                Log.e("StockRepository", "Error en sincronización en tiempo real de inventario: ${e.message}")
                _cloudSyncState.value = CloudSyncState.OFFLINE_LOCAL
            }
        }

        // Sincronización en tiempo real de técnicos
        scope.launch(Dispatchers.IO) {
            try {
                service.observeRemoteTechnicians().collect { remoteTechs ->
                    if (remoteTechs.isNotEmpty()) {
                        technicianDao.insertTechnicians(remoteTechs)
                    }
                }
            } catch (e: Exception) {
                Log.e("StockRepository", "Error en sincronización de técnicos: ${e.message}")
            }
        }
    }

    suspend fun updateQuantity(
        itemId: String,
        newQuantity: Int,
        technicianName: String = "TÉCNICO"
    ) = withContext(Dispatchers.IO) {
        val safeQty = newQuantity.coerceAtLeast(0)
        // 1. Actualización inmediata en base de datos local (0 latencia)
        stockDao.updateQuantity(itemId, safeQty)

        // 2. Sincronización en tiempo real hacia Firestore
        firestoreService?.let { service ->
            if (service.isAvailable) {
                _cloudSyncState.value = CloudSyncState.SYNCING
                service.updateRemoteQuantity(itemId, safeQty, technicianName)
                _cloudSyncState.value = CloudSyncState.ONLINE_SYNCED
            }
        }
    }

    suspend fun updateMinimumQuantity(itemId: String, newMinimum: Int) = withContext(Dispatchers.IO) {
        val safeMin = newMinimum.coerceAtLeast(0)
        stockDao.updateMinimumQuantity(itemId, safeMin)

        firestoreService?.let { service ->
            if (service.isAvailable) {
                val allItems = stockDao.getAllStockItems().firstOrNull()
                val updated = allItems?.find { it.id == itemId }?.copy(minimumQuantity = safeMin)
                if (updated != null) {
                    service.syncStockItem(updated)
                }
            }
        }
    }

    suspend fun addNewItemToVehicle(
        vehicleId: String,
        name: String,
        category: String,
        minimumQuantity: Int,
        initialQuantity: Int,
        unit: String = "uds",
        technicianName: String = "TÉCNICO"
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

        firestoreService?.let { service ->
            if (service.isAvailable) {
                service.syncStockItem(item, technicianName)
            }
        }
    }

    suspend fun updateStockItem(item: StockItem) = withContext(Dispatchers.IO) {
        stockDao.updateStockItem(item)
        firestoreService?.syncStockItem(item)
    }

    suspend fun restoreVehicleToMinimums(
        vehicleId: String,
        reviewerName: String = "Reposición de Mínimos"
    ) = withContext(Dispatchers.IO) {
        stockDao.restoreVehicleToMinimums(vehicleId)
        vehicleDao.updateRevision(vehicleId, System.currentTimeMillis(), reviewerName)

        firestoreService?.let { service ->
            if (service.isAvailable) {
                val updatedItems = stockDao.getItemsForVehicle(vehicleId).firstOrNull() ?: emptyList()
                service.pushBatchToFirestore(updatedItems, reviewerName)
            }
        }
    }

    suspend fun confirmRevision(vehicleId: String, reviewerName: String) = withContext(Dispatchers.IO) {
        vehicleDao.updateRevision(vehicleId, System.currentTimeMillis(), reviewerName)
    }

    fun getRevisionsForVehicle(vehicleId: String): Flow<List<RevisionRecord>> {
        return revisionDao.getRevisionsForVehicle(vehicleId)
    }

    suspend fun addTechnician(name: String, number: String): Result<Technician> = withContext(Dispatchers.IO) {
        val cleanName = name.trim().uppercase()
        val cleanNumber = number.trim()
        if (cleanName.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("El nombre del técnico no puede estar vacío."))
        }
        if (cleanNumber.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("El número de técnico no puede estar vacío."))
        }

        val deterministicId = "tech_${cleanNumber.padStart(2, '0')}"
        val tech = Technician(
            id = deterministicId,
            name = cleanName,
            number = cleanNumber
        )
        technicianDao.insertTechnician(tech)
        firestoreService?.saveRemoteTechnician(tech)
        Result.success(tech)
    }

    suspend fun deleteTechnician(technician: Technician): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            technicianDao.deleteTechnician(technician)
            firestoreService?.deleteRemoteTechnician(technician.id)
            Unit
        }
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

        // Auditoría centralizada en Firestore
        firestoreService?.recordRemoteRevision(record)
    }

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        if (vehicleDao.getCount() == 0 || stockDao.getCount() == 0) {
            AppDatabase.seedDatabase(database)
        } else {
            vehicleDao.insertVehicles(AppDatabase.getDefaultVehicles())

            val vehicles = AppDatabase.getDefaultVehicles()
            for (vehicle in vehicles) {
                val isVan = vehicle.type.contains("Furgoneta", ignoreCase = true)
                addNewItemToVehicle(
                    vehicleId = vehicle.id,
                    name = "Cajón portamonedas 41x41",
                    category = "Periféricos",
                    minimumQuantity = if (isVan) 1 else 0,
                    initialQuantity = if (isVan) 1 else 0,
                    unit = "uds"
                )
            }
        }

        // Limpieza garantizada de historiales de revisiones ficticios previos e inicio de inventario al 100%
        revisionDao.deleteAllRevisions()
        vehicleDao.resetAllRevisionsMetadata()
        stockDao.restoreAllToMinimums()

        // Si Firestore está disponible, sincronizar datos iniciales si la colección estuviera vacía
        firestoreService?.let { service ->
            if (service.isAvailable) {
                val allLocalItems = stockDao.getAllStockItems().firstOrNull() ?: emptyList()
                if (allLocalItems.isNotEmpty()) {
                    service.pushBatchToFirestore(allLocalItems, "Sistema Inicial AJCASH")
                }
            }
        }
    }
}
