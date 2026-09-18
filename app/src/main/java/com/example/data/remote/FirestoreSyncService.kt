package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.RevisionRecord
import com.example.data.model.StockItem
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class CloudSyncState {
    ONLINE_SYNCED,
    SYNCING,
    OFFLINE_LOCAL,
    UNCONFIGURED
}

class FirestoreSyncService(private val context: Context) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                Log.i(TAG, "FirebaseApp no inicializado. Operando en modo local seguro.")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error inicializando Firestore: ${e.message}")
            null
        }
    }

    val isAvailable: Boolean
        get() = firestore != null

    /**
     * Escucha en TIEMPO REAL los cambios de inventario desde Firestore.
     * Cuando otro técnico modifica el stock en su móvil, se emiten los cambios inmediatamente.
     */
    fun observeRemoteInventory(vehicleId: String? = null): Flow<List<FirestoreStockItem>> {
        val db = firestore ?: return flowOf(emptyList())

        return callbackFlow {
            val query = if (vehicleId != null) {
                db.collection(COLLECTION_INVENTORY).whereEqualTo("vehicleId", vehicleId)
            } else {
                db.collection(COLLECTION_INVENTORY)
            }

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error escuchando inventario Firestore: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FirestoreStockItem::class.java)?.copy(id = doc.id)
                    }
                    trySend(items)
                }
            }

            awaitClose {
                listener.remove()
            }
        }
    }

    /**
     * Sube o actualiza un artículo en Firestore.
     */
    suspend fun syncStockItem(item: StockItem, updatedBy: String = ""): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)

        runCatching {
            val firestoreItem = FirestoreStockItem.fromStockItem(item, updatedBy)
            db.collection(COLLECTION_INVENTORY)
                .document(item.id)
                .set(firestoreItem, SetOptions.merge())
                .await()
            Unit
        }
    }

    /**
     * Actualiza la cantidad de stock en Firestore con transacción o update.
     */
    suspend fun updateRemoteQuantity(
        itemId: String,
        newQuantity: Int,
        updatedBy: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)

        runCatching {
            val docRef = db.collection(COLLECTION_INVENTORY).document(itemId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    transaction.update(
                        docRef,
                        mapOf(
                            "currentQuantity" to newQuantity.coerceAtLeast(0),
                            "lastUpdatedBy" to updatedBy,
                            "updatedAtTimestamp" to System.currentTimeMillis()
                        )
                    )
                }
            }.await()
            Unit
        }
    }

    /**
     * Sincroniza en lote (batch) múltiples artículos a Firestore.
     */
    suspend fun pushBatchToFirestore(items: List<StockItem>, updatedBy: String = ""): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)

        runCatching {
            val batch = db.batch()
            for (item in items) {
                val docRef = db.collection(COLLECTION_INVENTORY).document(item.id)
                val firestoreItem = FirestoreStockItem.fromStockItem(item, updatedBy)
                batch.set(docRef, firestoreItem, SetOptions.merge())
            }
            batch.commit().await()
            Unit
        }
    }

    /**
     * Registra una revisión técnica en la nube para auditoría centralizada de guardia.
     */
    suspend fun recordRemoteRevision(record: RevisionRecord): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)

        runCatching {
            db.collection(COLLECTION_REVISIONS)
                .add(
                    mapOf(
                        "vehicleId" to record.vehicleId,
                        "technicianName" to record.technicianName,
                        "technicianNumber" to record.technicianNumber,
                        "timestamp" to record.timestamp,
                        "changesSummary" to record.changesSummary,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Unit
        }
    }

    /**
     * Sincroniza un técnico en Firestore.
     */
    suspend fun saveRemoteTechnician(technician: com.example.data.model.Technician): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)
        runCatching {
            db.collection(COLLECTION_TECHNICIANS)
                .document(technician.id)
                .set(
                    mapOf(
                        "id" to technician.id,
                        "name" to technician.name,
                        "number" to technician.number,
                        "createdAt" to technician.createdAt
                    ),
                    SetOptions.merge()
                )
                .await()
            Unit
        }
    }

    /**
     * Elimina un técnico en Firestore.
     */
    suspend fun deleteRemoteTechnician(technicianId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.success(Unit)
        runCatching {
            db.collection(COLLECTION_TECHNICIANS)
                .document(technicianId)
                .delete()
                .await()
            Unit
        }
    }

    /**
     * Observa técnicos registrados en Firestore en tiempo real.
     */
    fun observeRemoteTechnicians(): Flow<List<com.example.data.model.Technician>> {
        val db = firestore ?: return flowOf(emptyList())
        return callbackFlow {
            val listener = db.collection(COLLECTION_TECHNICIANS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error escuchando técnicos en Firestore: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val techs = snapshot.documents.mapNotNull { doc ->
                            val name = doc.getString("name") ?: return@mapNotNull null
                            val number = doc.getString("number") ?: ""
                            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            com.example.data.model.Technician(
                                id = doc.id,
                                name = name,
                                number = number,
                                createdAt = createdAt
                            )
                        }
                        trySend(techs)
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    companion object {
        private const val TAG = "FirestoreSyncService"
        const val COLLECTION_INVENTORY = "inventory"
        const val COLLECTION_REVISIONS = "revisions"
        const val COLLECTION_VEHICLES = "vehicles"
        const val COLLECTION_TECHNICIANS = "technicians"
    }
}
