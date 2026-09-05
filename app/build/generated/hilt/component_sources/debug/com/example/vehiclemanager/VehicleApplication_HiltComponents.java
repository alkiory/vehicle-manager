package com.example.vehiclemanager;

import com.example.vehiclemanager.core.data.database.BackupDataModule;
import com.example.vehiclemanager.core.data.database.DatabaseModule;
import com.example.vehiclemanager.core.data.fuel.FuelDataModule;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceDataModule;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleDataModule;
import com.example.vehiclemanager.core.data.theme.ThemeDataModule;
import com.example.vehiclemanager.core.data.vehicle.ActiveVehicleDataModule;
import com.example.vehiclemanager.core.data.vehicle.VehicleDataModule;
import com.example.vehiclemanager.feature.dashboard.DashboardViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.AddFuelViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.FuelDetailViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel_HiltModules;
import com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel_HiltModules;
import com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel_HiltModules;
import com.example.vehiclemanager.feature.settings.SettingsViewModel_HiltModules;
import com.example.vehiclemanager.feature.statistics.StatisticsViewModel_HiltModules;
import com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel_HiltModules;
import com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel_HiltModules;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Subcomponent;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.components.ViewComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.components.ViewWithFragmentComponent;
import dagger.hilt.android.flags.FragmentGetContextFix;
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_DefaultViewModelFactories_ActivityModule;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ViewModelModule;
import dagger.hilt.android.internal.managers.ActivityComponentManager;
import dagger.hilt.android.internal.managers.FragmentComponentManager;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleModule;
import dagger.hilt.android.internal.managers.HiltWrapper_SavedStateHandleModule;
import dagger.hilt.android.internal.managers.ServiceComponentManager;
import dagger.hilt.android.internal.managers.ViewComponentManager;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.HiltWrapper_ActivityModule;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.FragmentScoped;
import dagger.hilt.android.scopes.ServiceScoped;
import dagger.hilt.android.scopes.ViewModelScoped;
import dagger.hilt.android.scopes.ViewScoped;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedComponent;
import dagger.hilt.migration.DisableInstallInCheck;
import javax.annotation.processing.Generated;
import javax.inject.Singleton;

@Generated("dagger.hilt.processor.internal.root.RootProcessor")
public final class VehicleApplication_HiltComponents {
  private VehicleApplication_HiltComponents() {
  }

  @Module(
      subcomponents = ServiceC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ServiceCBuilderModule {
    @Binds
    ServiceComponentBuilder bind(ServiceC.Builder builder);
  }

  @Module(
      subcomponents = ActivityRetainedC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityRetainedCBuilderModule {
    @Binds
    ActivityRetainedComponentBuilder bind(ActivityRetainedC.Builder builder);
  }

  @Module(
      subcomponents = ActivityC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityCBuilderModule {
    @Binds
    ActivityComponentBuilder bind(ActivityC.Builder builder);
  }

  @Module(
      subcomponents = ViewModelC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewModelCBuilderModule {
    @Binds
    ViewModelComponentBuilder bind(ViewModelC.Builder builder);
  }

  @Module(
      subcomponents = ViewC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewCBuilderModule {
    @Binds
    ViewComponentBuilder bind(ViewC.Builder builder);
  }

  @Module(
      subcomponents = FragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface FragmentCBuilderModule {
    @Binds
    FragmentComponentBuilder bind(FragmentC.Builder builder);
  }

  @Module(
      subcomponents = ViewWithFragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewWithFragmentCBuilderModule {
    @Binds
    ViewWithFragmentComponentBuilder bind(ViewWithFragmentC.Builder builder);
  }

  @Component(
      modules = {
          ActiveVehicleDataModule.class,
          ApplicationContextModule.class,
          BackupDataModule.class,
          DatabaseModule.class,
          FuelDataModule.class,
          HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule.class,
          MaintenanceDataModule.class,
          MaintenanceScheduleDataModule.class,
          ThemeDataModule.class,
          ActivityRetainedCBuilderModule.class,
          ServiceCBuilderModule.class,
          VehicleDataModule.class
      }
  )
  @Singleton
  public abstract static class SingletonC implements VehicleApplication_GeneratedInjector,
      FragmentGetContextFix.FragmentGetContextFixEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint,
      ServiceComponentManager.ServiceComponentBuilderEntryPoint,
      SingletonComponent,
      GeneratedComponent {
  }

  @Subcomponent
  @ServiceScoped
  public abstract static class ServiceC implements ServiceComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ServiceComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          AddEditVehicleViewModel_HiltModules.KeyModule.class,
          AddFuelViewModel_HiltModules.KeyModule.class,
          AddMaintenanceViewModel_HiltModules.KeyModule.class,
          DashboardViewModel_HiltModules.KeyModule.class,
          FuelDetailViewModel_HiltModules.KeyModule.class,
          FuelHistoryViewModel_HiltModules.KeyModule.class,
          HiltWrapper_ActivityRetainedComponentManager_LifecycleModule.class,
          HiltWrapper_SavedStateHandleModule.class,
          MaintenanceHistoryViewModel_HiltModules.KeyModule.class,
          SettingsViewModel_HiltModules.KeyModule.class,
          StatisticsViewModel_HiltModules.KeyModule.class,
          ActivityCBuilderModule.class,
          ViewModelCBuilderModule.class,
          VehicleBackupViewModel_HiltModules.KeyModule.class
      }
  )
  @ActivityRetainedScoped
  public abstract static class ActivityRetainedC implements ActivityRetainedComponent,
      ActivityComponentManager.ActivityComponentBuilderEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityRetainedComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          HiltWrapper_ActivityModule.class,
          HiltWrapper_DefaultViewModelFactories_ActivityModule.class,
          FragmentCBuilderModule.class,
          ViewCBuilderModule.class
      }
  )
  @ActivityScoped
  public abstract static class ActivityC implements MainActivity_GeneratedInjector,
      ActivityComponent,
      DefaultViewModelFactories.ActivityEntryPoint,
      HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint,
      FragmentComponentManager.FragmentComponentBuilderEntryPoint,
      ViewComponentManager.ViewComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          AddEditVehicleViewModel_HiltModules.BindsModule.class,
          AddFuelViewModel_HiltModules.BindsModule.class,
          AddMaintenanceViewModel_HiltModules.BindsModule.class,
          DashboardViewModel_HiltModules.BindsModule.class,
          FuelDetailViewModel_HiltModules.BindsModule.class,
          FuelHistoryViewModel_HiltModules.BindsModule.class,
          HiltWrapper_HiltViewModelFactory_ViewModelModule.class,
          MaintenanceHistoryViewModel_HiltModules.BindsModule.class,
          SettingsViewModel_HiltModules.BindsModule.class,
          StatisticsViewModel_HiltModules.BindsModule.class,
          VehicleBackupViewModel_HiltModules.BindsModule.class
      }
  )
  @ViewModelScoped
  public abstract static class ViewModelC implements ViewModelComponent,
      HiltViewModelFactory.ViewModelFactoriesEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewModelComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewC implements ViewComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewComponentBuilder {
    }
  }

  @Subcomponent(
      modules = ViewWithFragmentCBuilderModule.class
  )
  @FragmentScoped
  public abstract static class FragmentC implements FragmentComponent,
      DefaultViewModelFactories.FragmentEntryPoint,
      ViewComponentManager.ViewWithFragmentComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends FragmentComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewWithFragmentC implements ViewWithFragmentComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewWithFragmentComponentBuilder {
    }
  }
}
