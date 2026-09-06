package com.example.vehiclemanager.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 4 to 5: Add notification tables.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create notification_schedules table
        database.execSQL("""
            CREATE TABLE notification_schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vehicleId INTEGER NOT NULL,
                serviceTitle TEXT NOT NULL,
                scheduledDateMs INTEGER NOT NULL,
                isDismissed INTEGER NOT NULL DEFAULT 0,
                isTriggered INTEGER NOT NULL DEFAULT 0,
                triggeredDateMs INTEGER,
                maintenanceScheduleId INTEGER
            )
        """)

        // Create notification_preferences table
        database.execSQL("""
            CREATE TABLE notification_preferences (
                id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                maintenanceRemindersEnabled INTEGER NOT NULL DEFAULT 1,
                fuelPriceAlertsEnabled INTEGER NOT NULL DEFAULT 1,
                reminderDaysBeforeDue INTEGER NOT NULL DEFAULT 7,
                fuelPriceAlertThreshold INTEGER NOT NULL DEFAULT 0
            )
        """)

        // Insert default preferences
        database.execSQL("""
            INSERT INTO notification_preferences (id, maintenanceRemindersEnabled, fuelPriceAlertsEnabled, reminderDaysBeforeDue, fuelPriceAlertThreshold)
            VALUES (1, 1, 1, 7, 0)
        """)
    }
}
