package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.RevisionRecord
import com.example.data.model.StockItem
import com.example.data.model.Technician
import com.example.data.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Vehicle::class, StockItem::class, RevisionRecord::class, Technician::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun stockDao(): StockDao
    abstract fun revisionDao(): RevisionDao
    abstract fun technicianDao(): TechnicianDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ajcash_stock_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database)
                    }
                }
            }
        }

        fun getDefaultVehicles(): List<Vehicle> {
            return listOf(
                Vehicle(
                    id = "coche_1",
                    name = "Coche 1",
                    model = "Opel Corsa",
                    type = "Coche de empresa",
                    plate = "0315-JVP",
                    imageDrawableName = "vehicle_car_1",
                    lastRevisionTimestamp = System.currentTimeMillis() - (1000 * 60 * 45), // 45 min ago
                    lastReviewedBy = "Carlos M. (Técnico)"
                ),
                Vehicle(
                    id = "coche_2",
                    name = "Coche 2",
                    model = "Clio",
                    type = "Coche de empresa",
                    plate = "8268-HVX",
                    imageDrawableName = "vehicle_car_2",
                    lastRevisionTimestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 6), // 6 hours ago
                    lastReviewedBy = "Javier S. (Técnico)"
                ),
                Vehicle(
                    id = "furgoneta_1",
                    name = "Furgoneta 1",
                    model = "Peugeot Bipper",
                    type = "Furgoneta de empresa",
                    plate = "9101-HYL",
                    imageDrawableName = "vehicle_van_1",
                    lastRevisionTimestamp = System.currentTimeMillis() - (1000 * 60 * 120), // 2 hours ago
                    lastReviewedBy = "Marcos R. (Técnico)"
                ),
                Vehicle(
                    id = "furgoneta_2",
                    name = "Furgoneta 2",
                    model = "Renault Express",
                    type = "Furgoneta de empresa",
                    plate = "1025-MJH",
                    imageDrawableName = "vehicle_van_2",
                    lastRevisionTimestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24), // 1 day ago
                    lastReviewedBy = "Pablo B. (Guardia)"
                )
            )
        }

        suspend fun seedDatabase(database: AppDatabase) {
            val vehicles = getDefaultVehicles()
            database.vehicleDao().insertVehicles(vehicles)

            val stockItems = mutableListOf<StockItem>()

            for (vehicle in vehicles) {
                val isVan = vehicle.type.contains("Furgoneta", ignoreCase = true)

                val templates = listOf(
                    // Cables Ethernet (todas las medidas de 0,5m a 5m) -> Mínimo 2 ud en todos
                    StockRule("Cables", "Cable Ethernet 0,5m", 2, "uds", defaultQty = if (vehicle.id == "coche_2") 1 else 3),
                    StockRule("Cables", "Cable Ethernet 1m", 2, "uds", defaultQty = 4),
                    StockRule("Cables", "Cable Ethernet 2m", 2, "uds", defaultQty = 3),
                    StockRule("Cables", "Cable Ethernet 3m", 2, "uds", defaultQty = 2),
                    StockRule("Cables", "Cable Ethernet 5m", 2, "uds", defaultQty = 2),
                    // TPVs
                    StockRule("TPVs", "TPV Modelo x500", 0, "uds", defaultQty = 1),
                    StockRule("TPVs", "TPV Modelo x205", 0, "uds", defaultQty = 0),
                    // TPVs x300: 1 en Furgonetas | 0 en Coches
                    StockRule("TPVs", "TPV Modelo x300", if (isVan) 1 else 0, "uds", defaultQty = if (isVan) 1 else 0),
                    // Impresoras CP-450: Mínimo 2 ud en todos
                    StockRule("Impresoras", "Impresora CP-450", 2, "uds", defaultQty = 2),
                    // Periféricos: Teclado + Ratón -> Mínimo 1 pack en todos
                    StockRule("Periféricos", "Conjunto Teclado + Ratón", 1, "pack", defaultQty = 1),
                    // Red: Switch 5 y 8 puertos -> Mínimo 1 ud en todos
                    StockRule("Red", "Switch 5 puertos", 1, "uds", defaultQty = 2),
                    StockRule("Red", "Switch 8 puertos", 1, "uds", defaultQty = if (vehicle.id == "coche_2") 0 else 1),
                    // Consumibles: Papel térmico 80x80 y 60x55 -> Mínimo 10 rollos en todos
                    StockRule("Consumibles", "Papel térmico 80x80", 10, "rollos", defaultQty = if (vehicle.id == "furgoneta_2") 6 else 12),
                    StockRule("Consumibles", "Papel térmico 60x55", 10, "rollos", defaultQty = 10),
                    // NUEVO OBJETO BASE: Cajón portamonedas 41x41 -> 1 en Furgonetas | 0 en Coches
                    StockRule("Periféricos", "Cajón portamonedas 41x41", if (isVan) 1 else 0, "uds", defaultQty = if (isVan) 1 else 0)
                )

                for (rule in templates) {
                    val deterministicId = "${vehicle.id}_${rule.category.lowercase()}_${rule.name.lowercase().replace(" ", "_").replace(",", "_").replace("+", "_")}"
                    stockItems.add(
                        StockItem(
                            id = deterministicId,
                            vehicleId = vehicle.id,
                            category = rule.category,
                            name = rule.name,
                            currentQuantity = rule.defaultQty,
                            minimumQuantity = rule.min,
                            unit = rule.unit
                        )
                    )
                }
            }

            database.stockDao().insertStockItems(stockItems)

            // Auditoría e Historial de Revisiones inicial
            val initialRevisions = listOf(
                RevisionRecord(
                    id = "rev_coche_1_init",
                    vehicleId = "coche_1",
                    technicianName = "CARLOS MARTÍNEZ",
                    technicianNumber = "12",
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 45),
                    changesSummary = "Sin cambios · Stock verificado conforme"
                ),
                RevisionRecord(
                    id = "rev_coche_2_init",
                    vehicleId = "coche_2",
                    technicianName = "JAVIER SANZ",
                    technicianNumber = "08",
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 6),
                    changesSummary = "Se restaron 2 uds de Papel térmico 80x80\nSe repuso 1 ud de Switch 5 puertos"
                ),
                RevisionRecord(
                    id = "rev_furgoneta_1_init",
                    vehicleId = "furgoneta_1",
                    technicianName = "MARCOS R.",
                    technicianNumber = "04",
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 120),
                    changesSummary = "Se añadió 1 ud de Cajón portamonedas 41x41\nSe añadió 1 ud de TPV Modelo x300"
                ),
                RevisionRecord(
                    id = "rev_furgoneta_2_init",
                    vehicleId = "furgoneta_2",
                    technicianName = "PABLO BLANCO",
                    technicianNumber = "16",
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                    changesSummary = "Sin cambios · Guardia iniciada OK"
                )
            )
            database.revisionDao().insertRevisions(initialRevisions)

            // La lista de técnicos se inicia vacía por requerimiento del usuario
        }

        private data class StockRule(
            val category: String,
            val name: String,
            val min: Int,
            val unit: String,
            val defaultQty: Int
        )
    }
}
