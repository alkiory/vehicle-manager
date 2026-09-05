package com.example.vehiclemanager.core.data.database;

import com.example.vehiclemanager.core.data.vehicle.VehicleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideVehicleDaoFactory implements Factory<VehicleDao> {
  private final Provider<VehicleDatabase> databaseProvider;

  public DatabaseModule_ProvideVehicleDaoFactory(Provider<VehicleDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public VehicleDao get() {
    return provideVehicleDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideVehicleDaoFactory create(
      Provider<VehicleDatabase> databaseProvider) {
    return new DatabaseModule_ProvideVehicleDaoFactory(databaseProvider);
  }

  public static VehicleDao provideVehicleDao(VehicleDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVehicleDao(database));
  }
}
