package com.example.vehiclemanager.core.data.fuel

import com.example.vehiclemanager.core.domain.FuelRecord

fun FuelRecordEntity.toDomain(): FuelRecord = FuelRecord(
    id = id,
    vehicleId = vehicleId,
    timestampMs = timestampMs,
    odometerKm = odometerKm,
    litersX100 = litersX100,
    pricePerLiterCents = pricePerLiterCents,
    totalCostCents = totalCostCents,
    isFullTank = isFullTank,
    stationName = stationName,
    notes = notes,
)

fun FuelRecord.toEntity(): FuelRecordEntity = FuelRecordEntity(
    id = id,
    vehicleId = vehicleId,
    timestampMs = timestampMs,
    odometerKm = odometerKm,
    litersX100 = litersX100,
    pricePerLiterCents = pricePerLiterCents,
    totalCostCents = totalCostCents,
    isFullTank = isFullTank,
    stationName = stationName,
    notes = notes,
)
