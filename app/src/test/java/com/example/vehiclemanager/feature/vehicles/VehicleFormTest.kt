package com.example.vehiclemanager.feature.vehicles

import com.example.vehiclemanager.core.domain.FuelType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleFormTest {
    private val validForm = VehicleFormData(
        name = "Daily driver",
        make = "Toyota",
        model = "Prius",
        year = "2022",
        licensePlate = "ABC-123",
        vin = "JTDBR32E720000007",
        fuelType = FuelType.HYBRID,
        primaryOdometerKm = "42500",
    )

    @Test
    fun validFormHasNoValidationErrors() {
        val errors = validForm.validate(currentYear = 2026)

        assertFalse(errors.hasErrors)
        assertNull(errors.name)
        assertNull(errors.year)
        assertNull(errors.odometer)
    }

    @Test
    fun requiredFieldsAreReported() {
        val errors = VehicleFormData().validate(currentYear = 2026)

        assertTrue(errors.hasErrors)
        assertTrue(errors.name != null)
        assertTrue(errors.make != null)
        assertTrue(errors.model != null)
        assertTrue(errors.licensePlate != null)
    }

    @Test
    fun yearMustBeWithinSupportedRange() {
        val tooOld = validForm.copy(year = (MIN_VEHICLE_YEAR - 1).toString())
        val tooNew = validForm.copy(year = "2027")

        assertEquals("range", tooOld.validate(currentYear = 2026).year)
        assertEquals("range", tooNew.validate(currentYear = 2026).year)
    }

    @Test
    fun odometerMustBePositiveInteger() {
        val zero = validForm.copy(primaryOdometerKm = "0")
        val negative = validForm.copy(primaryOdometerKm = "-1")
        val invalid = validForm.copy(primaryOdometerKm = "42.5")

        assertEquals("positive", zero.validate(currentYear = 2026).odometer)
        assertEquals("positive", negative.validate(currentYear = 2026).odometer)
        assertEquals("invalid", invalid.validate(currentYear = 2026).odometer)
    }

    private fun assertEquals(expected: String, actual: String?) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
