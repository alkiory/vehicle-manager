package com.example.vehiclemanager.core.data.vehicle

import com.example.vehiclemanager.core.domain.Vehicle
import com.example.vehiclemanager.core.domain.VehicleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
) : VehicleRepository {
    override val vehicles: Flow<List<Vehicle>> = vehicleDao
        .observeAll()
        .map { entities -> entities.map(VehicleEntity::toDomain) }

    override suspend fun getVehicle(id: Long): Vehicle? = vehicleDao
        .findById(id)
        ?.toDomain()

    override suspend fun insertVehicle(vehicle: Vehicle): Long = vehicleDao
        .insert(vehicle.toEntity())

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.update(vehicle.toEntity())
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.delete(vehicle.toEntity())
    }
}
