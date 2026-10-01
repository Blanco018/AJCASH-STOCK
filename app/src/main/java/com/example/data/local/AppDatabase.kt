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
    version = 9,
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
                    lastRevisionTimestamp = null,
                    lastReviewedBy = ""
                ),
                Vehicle(
                    id = "coche_2",
                    name = "Coche 2",
                    model = "Clio",
                    type = "Coche de empresa",
                    plate = "8268-HVX",
                    imageDrawableName = "vehicle_car_2",
                    lastRevisionTimestamp = null,
                    lastReviewedBy = ""
                ),
                Vehicle(
                    id = "furgoneta_1",
                    name = "Furgoneta 1",
                    model = "Peugeot Bipper",
                    type = "Furgoneta de empresa",
                    plate = "9101-HYL",
                    imageDrawableName = "vehicle_van_1",
                    lastRevisionTimestamp = null,
                    lastReviewedBy = ""
                ),
                Vehicle(
                    id = "furgoneta_2",
                    name = "Furgoneta 2",
                    model = "Renault Express",
                    type = "Furgoneta de empresa",
                    plate = "1025-MJH",
                    imageDrawableName = "vehicle_van_2",
                    lastRevisionTimestamp = null,
                    lastReviewedBy = ""
                )
            )
        }

        suspend fun seedDatabase(database: AppDatabase) {
            val vehicles = getDefaultVehicles()
            database.vehicleDao().insertVehicles(vehicles)

            val stockItems = mutableListOf<StockItem>()

            for (vehicle in vehicles) {
                val isVan = vehicle.type.contains("Furgoneta", ignoreCase = true)

                // Cada vehículo inicia con stock al 100% (defaultQty = rule.min)
                val templates = listOf(
                    // Cables Ethernet (todas las medidas de 0,5m a 5m) -> Mínimo 2 ud en todos
                    StockRule("Cables", "Cable Ethernet 0,5m", 2, "uds", defaultQty = 2),
                    StockRule("Cables", "Cable Ethernet 1m", 2, "uds", defaultQty = 2),
                    StockRule("Cables", "Cable Ethernet 2m", 2, "uds", defaultQty = 2),
                    StockRule("Cables", "Cable Ethernet 3m", 2, "uds", defaultQty = 2),
                    StockRule("Cables", "Cable Ethernet 5m", 2, "uds", defaultQty = 2),
                    // TPVs
                    StockRule("TPVs", "TPV Modelo x500", 0, "uds", defaultQty = 0),
                    StockRule("TPVs", "TPV Modelo x205", 0, "uds", defaultQty = 0),
                    // TPVs x300: 1 en Furgonetas | 0 en Coches
                    StockRule("TPVs", "TPV Modelo x300", if (isVan) 1 else 0, "uds", defaultQty = if (isVan) 1 else 0),
                    // Impresoras CP-450: Mínimo 2 ud en todos
                    StockRule("Impresoras", "Impresora CP-450", 2, "uds", defaultQty = 2),
                    // Periféricos: Teclado + Ratón -> Mínimo 1 pack en todos
                    StockRule("Periféricos", "Conjunto Teclado + Ratón", 1, "pack", defaultQty = 1),
                    // Red: Switch 5 y 8 puertos -> Mínimo 1 ud en todos
                    StockRule("Red", "Switch 5 puertos", 1, "uds", defaultQty = 1),
                    StockRule("Red", "Switch 8 puertos", 1, "uds", defaultQty = 1),
                    // Consumibles: Papel térmico 80x80 y 60x55 -> Mínimo 10 rollos en todos
                    StockRule("Consumibles", "Papel térmico 80x80", 10, "rollos", defaultQty = 10),
                    StockRule("Consumibles", "Papel térmico 60x55", 10, "rollos", defaultQty = 10),
                    // Periféricos: Cajón portamonedas 41x41 -> 1 en Furgonetas | 0 en Coches
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

            // Técnico inicial de guardia por defecto
            database.technicianDao().insertTechnicians(
                listOf(
                    Technician(
                        id = "tech_pablo_blanco",
                        name = "PABLO BLANCO",
                        number = "16"
                    )
                )
            )
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
