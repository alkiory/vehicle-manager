package com.example.vehiclemanager.core.data.database

import com.example.vehiclemanager.core.domain.FuelType
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun instantRoundTripPreservesEpochMillis() {
        val instant = Instant.ofEpochMilli(1_735_689_600_123)

        assertEquals(instant.toEpochMilli(), converters.instantToEpochMillis(instant))
        assertEquals(instant, converters.epochMillisToInstant(instant.toEpochMilli()))
    }

    @Test
    fun nullableInstantValuesRemainNull() {
        assertNull(converters.instantToEpochMillis(null))
        assertNull(converters.epochMillisToInstant(null))
    }

    @Test
    fun fuelTypeRoundTripUsesStableName() {
        assertEquals("HYBRID", converters.fuelTypeToName(FuelType.HYBRID))
        assertEquals(FuelType.HYBRID, converters.nameToFuelType("HYBRID"))
    }

    @Test
    fun nullableFuelTypeValuesRemainNull() {
        assertNull(converters.fuelTypeToName(null))
        assertNull(converters.nameToFuelType(null))
    }
}
