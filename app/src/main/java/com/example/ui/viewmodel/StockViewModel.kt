package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.RevisionRecord
import com.example.data.model.StockItem
import com.example.data.model.Technician
import com.example.data.model.Vehicle
import com.example.data.model.VehicleStockSummary
import com.example.data.remote.CloudSyncState
import com.example.data.remote.FirestoreSyncService
import com.example.data.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StockRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        val firestoreService = FirestoreSyncService(application)
        repository = StockRepository(
            database = database,
            vehicleDao = database.vehicleDao(),
            stockDao = database.stockDao(),
            firestoreService = firestoreService
        )
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
            repository.startRealtimeCloudSync(viewModelScope)
        }

        viewModelScope.launch {
            repository.allTechnicians.collect { list ->
                if (list.isNotEmpty()) {
                    val currentSelected = _selectedTechnician.value
                    if (currentSelected == null || list.none { it.id == currentSelected.id }) {
                        val preferred = list.find { it.number == "16" || it.name.contains("PABLO", ignoreCase = true) }
                            ?: list.first()
                        selectTechnician(preferred)
                    }
                }
            }
        }
    }

    val cloudSyncState: StateFlow<CloudSyncState> = repository.cloudSyncState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CloudSyncState.OFFLINE_LOCAL
        )

    val allTechnicians: StateFlow<List<Technician>> = repository.allTechnicians
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTechnician = MutableStateFlow<Technician?>(null)
    val selectedTechnician: StateFlow<Technician?> = _selectedTechnician.asStateFlow()

    val vehiclesSummary: StateFlow<List<VehicleStockSummary>> =
        repository.vehiclesWithSummaries
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedVehicleId = MutableStateFlow<String?>(null)
    val selectedVehicleId: StateFlow<String?> = _selectedVehicleId.asStateFlow()

    // Datos del Técnico de Guardia activo
    private val _technicianName = MutableStateFlow("PABLO BLANCO")
    val technicianName: StateFlow<String> = _technicianName.asStateFlow()

    private val _technicianNumber = MutableStateFlow("16")
    val technicianNumber: StateFlow<String> = _technicianNumber.asStateFlow()

    // Snapshot para auditar deltas al guardar la revisión
    private val initialStockMap = mutableMapOf<String, Int>()

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyAlerts = MutableStateFlow(false)
    val showOnlyAlerts: StateFlow<Boolean> = _showOnlyAlerts.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>(
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    val currentVehicle: StateFlow<Vehicle?> = _selectedVehicleId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getVehicle(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allCurrentVehicleItems: StateFlow<List<StockItem>> = _selectedVehicleId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getItemsForVehicle(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentVehicleItems: StateFlow<List<StockItem>> =
        combine(
            _selectedVehicleId.flatMapLatest { id ->
                if (id == null) flowOf(emptyList()) else repository.getItemsForVehicle(id)
            },
            _selectedCategory,
            _searchQuery,
            _showOnlyAlerts
        ) { items, category, query, onlyAlerts ->
            items.filter { item ->
                val matchesCategory = (category == "Todas" || item.category.equals(category, ignoreCase = true))
                val matchesQuery = query.isBlank() || item.name.contains(query, ignoreCase = true) || item.category.contains(query, ignoreCase = true)
                val matchesAlert = !onlyAlerts || item.isUnderMinimum
                matchesCategory && matchesQuery && matchesAlert
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCategories = listOf(
        "Todas",
        "TPVs",
        "Impresoras",
        "Cables",
        "Consumibles",
        "Periféricos",
        "Red"
    )

    fun selectTechnician(technician: Technician) {
        _selectedTechnician.value = technician
        _technicianName.value = technician.name
        _technicianNumber.value = technician.number
    }

    fun createTechnician(name: String, number: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.addTechnician(name, number)
            result.onSuccess { tech ->
                selectTechnician(tech)
                _userMessage.tryEmit("Técnico '${tech.name} (Nº ${tech.number})' registrado correctamente")
                onComplete?.invoke(true, "Registrado")
            }.onFailure { error ->
                val msg = error.message ?: "Error al registrar técnico"
                _userMessage.tryEmit(msg)
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun deleteTechnician(technician: Technician) {
        viewModelScope.launch {
            val result = repository.deleteTechnician(technician)
            result.onSuccess {
                _userMessage.tryEmit("Técnico '${technician.name}' eliminado")
                if (_selectedTechnician.value?.id == technician.id) {
                    val remaining = allTechnicians.value.filter { it.id != technician.id }
                    if (remaining.isNotEmpty()) {
                        selectTechnician(remaining.first())
                    } else {
                        _selectedTechnician.value = null
                    }
                }
            }.onFailure { error ->
                _userMessage.tryEmit(error.message ?: "Error al eliminar técnico")
            }
        }
    }

    fun setTechnician(name: String, number: String) {
        _technicianName.value = name.trim().uppercase()
        _technicianNumber.value = number.trim()
    }

    fun selectVehicle(vehicleId: String?) {
        _selectedVehicleId.value = vehicleId
        _selectedCategory.value = "Todas"
        _searchQuery.value = ""
        _showOnlyAlerts.value = false
    }

    fun startVehicleInspection(vehicleId: String, name: String, number: String) {
        setTechnician(name, number)
        selectVehicle(vehicleId)
        initialStockMap.clear()
    }

    fun startVehicleInspectionWithTechnician(vehicleId: String, technician: Technician) {
        selectTechnician(technician)
        selectVehicle(vehicleId)
        initialStockMap.clear()
    }

    fun captureInitialStockSnapshot(items: List<StockItem>) {
        if (initialStockMap.isEmpty() && items.isNotEmpty()) {
            items.forEach { initialStockMap[it.id] = it.currentQuantity }
        }
    }

    fun getRevisionsForVehicle(vehicleId: String): Flow<List<RevisionRecord>> {
        return repository.getRevisionsForVehicle(vehicleId)
    }

    fun recordVehicleRevision(vehicleId: String, currentItems: List<StockItem>) {
        viewModelScope.launch {
            val changes = mutableListOf<String>()

            for (item in currentItems) {
                val initialQty = initialStockMap[item.id] ?: item.currentQuantity
                val diff = item.currentQuantity - initialQty
                if (diff > 0) {
                    changes.add("Se añadió/repuso $diff ${item.unit} de ${item.name}")
                } else if (diff < 0) {
                    changes.add("Se restaron ${kotlin.math.abs(diff)} ${item.unit} de ${item.name}")
                }
            }

            if (changes.isEmpty()) {
                changes.add("Sin cambios · Stock verificado conforme")
            }

            val techName = _technicianName.value.ifBlank { "TÉCNICO" }
            val techNum = _technicianNumber.value.ifBlank { "16" }

            repository.recordRevision(
                vehicleId = vehicleId,
                technicianName = techName,
                technicianNumber = techNum,
                changes = changes
            )

            // Actualizar snapshot con las nuevas cantidades
            initialStockMap.clear()
            currentItems.forEach { initialStockMap[it.id] = it.currentQuantity }

            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _userMessage.tryEmit("✓ Revisión registrada para $techName (#$techNum) a las $time")
        }
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleOnlyAlerts() {
        _showOnlyAlerts.value = !_showOnlyAlerts.value
    }

    private fun currentTechLabel(): String =
        "${_technicianName.value.ifBlank { "TÉCNICO" }} (#${_technicianNumber.value.ifBlank { "16" }})"

    fun incrementQuantity(item: StockItem) {
        viewModelScope.launch {
            repository.updateQuantity(item.id, item.currentQuantity + 1, currentTechLabel())
        }
    }

    fun decrementQuantity(item: StockItem) {
        viewModelScope.launch {
            if (item.currentQuantity > 0) {
                repository.updateQuantity(item.id, item.currentQuantity - 1, currentTechLabel())
            }
        }
    }

    fun setQuantity(item: StockItem, quantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(item.id, quantity.coerceAtLeast(0), currentTechLabel())
        }
    }

    fun updateMinimumQuantity(item: StockItem, newMinimum: Int) {
        viewModelScope.launch {
            val safeMin = newMinimum.coerceAtLeast(0)
            repository.updateMinimumQuantity(item.id, safeMin)
            _userMessage.tryEmit("Mínimo de '${item.name}' actualizado a $safeMin ${item.unit}")
        }
    }

    fun addNewItem(
        vehicleId: String,
        name: String,
        category: String,
        minimumQuantity: Int,
        initialQuantity: Int,
        unit: String = "uds"
    ) {
        viewModelScope.launch {
            val safeMin = minimumQuantity.coerceAtLeast(0)
            val safeQty = initialQuantity.coerceAtLeast(0)
            repository.addNewItemToVehicle(
                vehicleId = vehicleId,
                name = name,
                category = category,
                minimumQuantity = safeMin,
                initialQuantity = safeQty,
                unit = unit,
                technicianName = currentTechLabel()
            )
            _userMessage.tryEmit("'$name' añadido al inventario con mínimo de $safeMin $unit")
        }
    }

    fun restoreAllToMinimums(vehicleId: String) {
        viewModelScope.launch {
            repository.restoreVehicleToMinimums(vehicleId)
            _userMessage.tryEmit("Materiales repuestos al stock mínimo de guardia")
        }
    }

    fun confirmRevision(vehicleId: String, reviewerName: String = "Técnico de Guardia") {
        viewModelScope.launch {
            repository.confirmRevision(vehicleId, reviewerName)
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _userMessage.tryEmit("Revisión guardada y confirmada a las $time")
        }
    }
}
