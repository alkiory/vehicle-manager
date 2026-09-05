package com.example.vehiclemanager.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS fuel_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vehicleId INTEGER NOT NULL,
                timestampMs INTEGER NOT NULL,
                odometerKm INTEGER NOT NULL,
                litersX100 INTEGER NOT NULL,
                pricePerLiterCents INTEGER NOT NULL,
                totalCostCents INTEGER NOT NULL,
                isFullTank INTEGER NOT NULL,
                stationName TEXT,
                notes TEXT,
                FOREIGN KEY(vehicleId) REFERENCES vehicles(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_fuel_records_vehicleId_timestampMs " +
                "ON fuel_records(vehicleId, timestampMs)",
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS maintenance_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vehicleId INTEGER NOT NULL,
                title TEXT NOT NULL,
                category TEXT NOT NULL,
                costCents INTEGER NOT NULL,
                odometerKm INTEGER NOT NULL,
                timestampMs INTEGER NOT NULL,
                notes TEXT,
                performedBy TEXT,
                FOREIGN KEY(vehicleId) REFERENCES vehicles(id) ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_maintenance_records_vehicleId_timestampMs " +
                "ON maintenance_records(vehicleId, timestampMs)",
        )
    }
}
