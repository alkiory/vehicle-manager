package com.example.vehiclemanager.core.data.database

import androidx.room.TypeConverter
import com.example.vehiclemanager.core.domain.FuelType
import java.time.Instant

/**
 * Room conversions shared by all database entities.
 *
 * Monetary and quantity values are represented by Longs in entities, so Room
 * persists their minor-unit values without a lossy conversion. Enum values are
 * stored by stable name rather than ordinal.
 */
class Converters {
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fuelTypeToName(value: FuelType?): String? = value?.name

    @TypeConverter
    fun nameToFuelType(value: String?): FuelType? = value?.let(FuelType::valueOf)
}
