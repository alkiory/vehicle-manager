package com.example.vehiclemanager.feature.fuel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelFormTest {
    private val validForm = FuelFormData(
        odometerKm = "42500",
        liters = "45.25",
        pricePerLiter = "1.79",
    )

    @Test
    fun validFormHasNoErrors() {
        assertFalse(validForm.validate(previousOdometerKm = 42_000).hasErrors)
    }

    @Test
    fun odometerCannotBeLowerThanPreviousRefuel() {
        val errors = validForm.copy(odometerKm = "41999")
            .validate(previousOdometerKm = 42_000)

        assertEquals("lower_than_previous", errors.odometer)
    }

    @Test
    fun costAndQuantityUseIntegerMinorUnits() {
        assertEquals(4_525, litersToX100("45.25"))
        assertEquals(179L, calculatePricePerLiterCents("1.79"))
        assertEquals(8_100L, calculateTotalCostCents("45.25", "1.79"))
    }

    @Test
    fun invalidAndNonPositiveValuesAreRejected() {
        val errors = validForm.copy(
            odometerKm = "0",
            liters = "0",
            pricePerLiter = "not-a-number",
        ).validate(previousOdometerKm = null)

        assertTrue(errors.odometer != null)
        assertEquals("positive", errors.liters)
        assertEquals("invalid", errors.pricePerLiter)
    }

    @Test
    fun malformedCalculationInputsReturnNull() {
        assertEquals(null, litersToX100("bad"))
        assertEquals(null, calculatePricePerLiterCents("bad"))
        assertEquals(null, calculateTotalCostCents("1", "bad"))
    }
}
