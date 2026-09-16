package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.StockItem
import com.example.data.model.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Vehicle::class, StockItem::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun stockDao(): StockDao

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

            val stockTemplates = listOf(
                // TPVs
                StockTemplate("TPVs", "TPV Modelo x300", min = 2, unit = "uds"),
                StockTemplate("TPVs", "TPV Modelo x500", min = 2, unit = "uds"),
                StockTemplate("TPVs", "TPV Modelo x205", min = 1, unit = "uds"),
                // Impresoras
                StockTemplate("Impresoras", "Impresora 450", min = 2, unit = "uds"),
                // Cables Ethernet
                StockTemplate("Cables", "Cable Ethernet 0,5m", min = 3, unit = "uds"),
                StockTemplate("Cables", "Cable Ethernet 1m", min = 4, unit = "uds"),
                StockTemplate("Cables", "Cable Ethernet 2m", min = 4, unit = "uds"),
                StockTemplate("Cables", "Cable Ethernet 3m", min = 3, unit = "uds"),
                StockTemplate("Cables", "Cable Ethernet 5m", min = 2, unit = "uds"),
                // Periféricos
                StockTemplate("Periféricos", "Conjunto Teclado + Ratón", min = 2, unit = "packs"),
                // Consumibles
                StockTemplate("Consumibles", "Papel térmico 80x80", min = 10, unit = "rollos"),
                StockTemplate("Consumibles", "Papel térmico 60x55", min = 10, unit = "rollos"),
                // Red
                StockTemplate("Red", "Switch 5 puertos", min = 2, unit = "uds"),
                StockTemplate("Red", "Switch 8 puertos", min = 1, unit = "uds")
            )

            val stockItems = mutableListOf<StockItem>()

            for (vehicle in vehicles) {
                for (tmpl in stockTemplates) {
                    val qty = when {
                        vehicle.id == "coche_1" -> tmpl.min + 1
                        vehicle.id == "furgoneta_1" -> tmpl.min + 2
                        vehicle.id == "coche_2" && tmpl.name == "Switch 8 puertos" -> 0
                        vehicle.id == "coche_2" && tmpl.name == "TPV Modelo x500" -> 1
                        vehicle.id == "coche_2" -> tmpl.min
                        vehicle.id == "furgoneta_2" && tmpl.name == "Papel térmico 80x80" -> 4
                        vehicle.id == "furgoneta_2" -> tmpl.min + 1
                        else -> tmpl.min
                    }
                    val deterministicId = "${vehicle.id}_${tmpl.category.lowercase()}_${tmpl.name.lowercase().replace(" ", "_").replace(",", "_").replace("+", "_")}"
                    stockItems.add(
                        StockItem(
                            id = deterministicId,
                            vehicleId = vehicle.id,
                            category = tmpl.category,
                            name = tmpl.name,
                            currentQuantity = qty,
                            minimumQuantity = tmpl.min,
                            unit = tmpl.unit
                        )
                    )
                }
            }

            database.stockDao().insertStockItems(stockItems)
        }

        private data class StockTemplate(
            val category: String,
            val name: String,
            val min: Int,
            val unit: String
        )
    }
}
