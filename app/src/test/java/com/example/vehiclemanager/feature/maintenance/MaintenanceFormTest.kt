package com.example.vehiclemanager.feature.maintenance

import com.example.vehiclemanager.core.domain.MaintenanceCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceFormTest {
    private val validForm = MaintenanceFormData(
        title = "Oil change",
        category = MaintenanceCategory.OIL_CHANGE,
        cost = "125.00",
        odometerKm = "50000",
    )

    @Test
    fun validFormHasNoErrors() {
        assertFalse(validForm.validate(previousOdometerKm = 49_000).hasErrors)
        assertEquals(12_500L, maintenanceCostToCents(validForm.cost))
    }

    @Test
    fun requiredAndInvalidValuesAreReported() {
        val errors = MaintenanceFormData().validate(previousOdometerKm = null)

        assertTrue(errors.title != null)
        assertEquals("invalid", errors.cost)
        assertEquals("invalid", errors.odometer)
    }

    @Test
    fun odometerCannotMoveBackwards() {
        val errors = validForm.copy(odometerKm = "49999")
            .validate(previousOdometerKm = 50_000)

        assertEquals("lower_than_previous", errors.odometer)
    }

    @Test
    fun odometerUpdateOnlyMovesForward() {
        assertEquals(50_000L, odometerAfterMaintenance(50_000, 49_000))
        assertEquals(52_000L, odometerAfterMaintenance(50_000, 52_000))
    }

    @Test
    fun malformedCostReturnsNull() {
        assertEquals(null, maintenanceCostToCents("not-a-cost"))
        assertEquals(null, maintenanceCostToCents("-1.00"))
    }
}
