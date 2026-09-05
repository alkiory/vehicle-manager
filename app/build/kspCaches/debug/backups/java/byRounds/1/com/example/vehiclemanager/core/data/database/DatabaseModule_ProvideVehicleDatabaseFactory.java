package com.example.vehiclemanager.core.data.database;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideVehicleDatabaseFactory implements Factory<VehicleDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideVehicleDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public VehicleDatabase get() {
    return provideVehicleDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideVehicleDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideVehicleDatabaseFactory(contextProvider);
  }

  public static VehicleDatabase provideVehicleDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVehicleDatabase(context));
  }
}
