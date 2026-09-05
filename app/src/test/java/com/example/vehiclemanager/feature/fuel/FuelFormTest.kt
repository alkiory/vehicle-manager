package com.example.vehiclemanager.feature.fuel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelFormTest {
    private val validForm = FuelFormData(
        odometerKm = "42500",
        liters = "45.25",
        pricePerLiter = "1.79",
        totalCost = "80.99",
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
    fun totalCostParsesIntoMinorUnits() {
        assertEquals(8_099L, calculateTotalCostCents("80.99"))
        assertEquals(8_100L, computeTotalCostFromParts("45.25", "1.79")?.valueMinor)
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

    @Test
    fun missingAmountFieldIsTheOneNotEdited() {
        assertNull(setOf<FuelAmountField>().missingAmountField())
        assertNull(setOf(FuelAmountField.LITERS, FuelAmountField.PRICE_PER_LITER, FuelAmountField.TOTAL_COST).missingAmountField())
        assertEquals(
            FuelAmountField.TOTAL_COST,
            setOf(FuelAmountField.LITERS, FuelAmountField.PRICE_PER_LITER).missingAmountField(),
        )
    }

    @Test
    fun computesThirdValueFromAnyTwoFields() {
        // Liters x Price = Total: 45.25 L x 1.79 €/L = 80.9975 € → 8100 cents
        assertEquals(8_100L, computeTotalCostFromParts("45.25", "1.79")?.valueMinor)
        // Total / Price = Liters: 80.99 € / 1.79 €/L = 45.24… L → 4525 liters x100 (45.25 L)
        assertEquals(4_525L, computeLiters("1.79", "80.99")?.valueMinor)
        assertEquals("45.25", computeLiters("1.79", "80.99")?.toUserText())
        // Total / Liters = Price: 80.99 € / 45.25 L = 1.79 €/L → 179 cents
        assertEquals(179L, computePricePerLiterCents("45.25", "80.99")?.valueMinor)
        assertEquals("1.79", computePricePerLiterCents("45.25", "80.99")?.toUserText())
    }

    @Test
    fun calculatorRejectsZeroAndMalformedInputs() {
        assertNull(computeTotalCostFromParts("0", "1.79"))
        assertNull(computeLiters("0", "80.99"))
        assertNull(computePricePerLiterCents("bad", "80.99"))
        assertNull(computeLiters("1.79", "bad"))
        assertNull(computePricePerLiterCents("0", "80.99"))
    }

    @Test
    fun computedAmountFormatsAsTwoFractionDigits() {
        assertEquals("45.25", ComputedAmount(4_525, FuelAmountField.LITERS).toUserText())
        assertEquals("0.05", ComputedAmount(5, FuelAmountField.TOTAL_COST).toUserText())
        assertEquals("12.00", ComputedAmount(1_200, FuelAmountField.PRICE_PER_LITER).toUserText())
    }
}
