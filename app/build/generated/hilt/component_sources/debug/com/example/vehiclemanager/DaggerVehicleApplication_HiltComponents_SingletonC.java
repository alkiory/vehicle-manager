package com.example.vehiclemanager;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.vehiclemanager.core.data.database.DatabaseModule_ProvideFuelRecordDaoFactory;
import com.example.vehiclemanager.core.data.database.DatabaseModule_ProvideMaintenanceRecordDaoFactory;
import com.example.vehiclemanager.core.data.database.DatabaseModule_ProvideMaintenanceScheduleDaoFactory;
import com.example.vehiclemanager.core.data.database.DatabaseModule_ProvideVehicleDaoFactory;
import com.example.vehiclemanager.core.data.database.DatabaseModule_ProvideVehicleDatabaseFactory;
import com.example.vehiclemanager.core.data.database.RoomVehicleBackupRepository;
import com.example.vehiclemanager.core.data.database.VehicleDatabase;
import com.example.vehiclemanager.core.data.fuel.FuelRecordDao;
import com.example.vehiclemanager.core.data.fuel.FuelRecordRepositoryImpl;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordDao;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordRepositoryImpl;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleDao;
import com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleRepositoryImpl;
import com.example.vehiclemanager.core.data.theme.ThemePreferenceRepositoryImpl;
import com.example.vehiclemanager.core.data.vehicle.ActiveVehicleDataModule_Companion_ProvideActiveVehicleDataStoreFactory;
import com.example.vehiclemanager.core.data.vehicle.ActiveVehicleRepositoryImpl;
import com.example.vehiclemanager.core.data.vehicle.VehicleDao;
import com.example.vehiclemanager.core.data.vehicle.VehicleRepositoryImpl;
import com.example.vehiclemanager.core.domain.CalculateVehicleStatsUseCase;
import com.example.vehiclemanager.core.domain.ExportDatabaseUseCase;
import com.example.vehiclemanager.core.domain.FuelRecordRepository;
import com.example.vehiclemanager.core.domain.GetDashboardSummaryUseCase;
import com.example.vehiclemanager.core.domain.ImportDatabaseUseCase;
import com.example.vehiclemanager.core.domain.MaintenanceRecordRepository;
import com.example.vehiclemanager.core.domain.MaintenanceScheduleRepository;
import com.example.vehiclemanager.core.domain.VehicleBackupRepository;
import com.example.vehiclemanager.core.domain.VehicleRepository;
import com.example.vehiclemanager.core.ui.navigation.AppScaffoldViewModel;
import com.example.vehiclemanager.core.ui.navigation.AppScaffoldViewModel_HiltModules;
import com.example.vehiclemanager.core.ui.navigation.AppScaffoldViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.core.ui.navigation.AppScaffoldViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.core.ui.navigation.FormDirtyStateHolder;
import com.example.vehiclemanager.feature.dashboard.DashboardViewModel;
import com.example.vehiclemanager.feature.dashboard.DashboardViewModel_HiltModules;
import com.example.vehiclemanager.feature.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.AddFuelViewModel;
import com.example.vehiclemanager.feature.fuel.AddFuelViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.AddFuelViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.AddFuelViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.FuelDetailViewModel;
import com.example.vehiclemanager.feature.fuel.FuelDetailViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.FuelDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.FuelDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel;
import com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel_HiltModules;
import com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel;
import com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel_HiltModules;
import com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel;
import com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel_HiltModules;
import com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.settings.SettingsViewModel;
import com.example.vehiclemanager.feature.settings.SettingsViewModel_HiltModules;
import com.example.vehiclemanager.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.statistics.StatisticsViewModel;
import com.example.vehiclemanager.feature.statistics.StatisticsViewModel_HiltModules;
import com.example.vehiclemanager.feature.statistics.StatisticsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.statistics.StatisticsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel;
import com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel_HiltModules;
import com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel;
import com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel_HiltModules;
import com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerVehicleApplication_HiltComponents_SingletonC {
  private DaggerVehicleApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public VehicleApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements VehicleApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements VehicleApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements VehicleApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements VehicleApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements VehicleApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements VehicleApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements VehicleApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public VehicleApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends VehicleApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends VehicleApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends VehicleApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends VehicleApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(11).put(AddEditVehicleViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddEditVehicleViewModel_HiltModules.KeyModule.provide()).put(AddFuelViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddFuelViewModel_HiltModules.KeyModule.provide()).put(AddMaintenanceViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddMaintenanceViewModel_HiltModules.KeyModule.provide()).put(AppScaffoldViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AppScaffoldViewModel_HiltModules.KeyModule.provide()).put(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide()).put(FuelDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FuelDetailViewModel_HiltModules.KeyModule.provide()).put(FuelHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FuelHistoryViewModel_HiltModules.KeyModule.provide()).put(MaintenanceHistoryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MaintenanceHistoryViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(StatisticsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, StatisticsViewModel_HiltModules.KeyModule.provide()).put(VehicleBackupViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, VehicleBackupViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectThemePreferenceRepository(instance, singletonCImpl.themePreferenceRepositoryImplProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends VehicleApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddEditVehicleViewModel> addEditVehicleViewModelProvider;

    private Provider<AddFuelViewModel> addFuelViewModelProvider;

    private Provider<AddMaintenanceViewModel> addMaintenanceViewModelProvider;

    private Provider<AppScaffoldViewModel> appScaffoldViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<FuelDetailViewModel> fuelDetailViewModelProvider;

    private Provider<FuelHistoryViewModel> fuelHistoryViewModelProvider;

    private Provider<MaintenanceHistoryViewModel> maintenanceHistoryViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<StatisticsViewModel> statisticsViewModelProvider;

    private Provider<VehicleBackupViewModel> vehicleBackupViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ExportDatabaseUseCase exportDatabaseUseCase() {
      return new ExportDatabaseUseCase(singletonCImpl.bindVehicleBackupRepositoryProvider.get());
    }

    private ImportDatabaseUseCase importDatabaseUseCase() {
      return new ImportDatabaseUseCase(singletonCImpl.bindVehicleBackupRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addEditVehicleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.addFuelViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.addMaintenanceViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.appScaffoldViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.fuelDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.fuelHistoryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.maintenanceHistoryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.statisticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.vehicleBackupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(11).put(AddEditVehicleViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) addEditVehicleViewModelProvider)).put(AddFuelViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) addFuelViewModelProvider)).put(AddMaintenanceViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) addMaintenanceViewModelProvider)).put(AppScaffoldViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) appScaffoldViewModelProvider)).put(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) dashboardViewModelProvider)).put(FuelDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) fuelDetailViewModelProvider)).put(FuelHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) fuelHistoryViewModelProvider)).put(MaintenanceHistoryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) maintenanceHistoryViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(StatisticsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) statisticsViewModelProvider)).put(VehicleBackupViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) vehicleBackupViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.vehiclemanager.feature.vehicles.AddEditVehicleViewModel 
          return (T) new AddEditVehicleViewModel(singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.formDirtyStateHolderProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // com.example.vehiclemanager.feature.fuel.AddFuelViewModel 
          return (T) new AddFuelViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindFuelRecordRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.formDirtyStateHolderProvider.get());

          case 2: // com.example.vehiclemanager.feature.maintenance.AddMaintenanceViewModel 
          return (T) new AddMaintenanceViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindMaintenanceRecordRepositoryProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.formDirtyStateHolderProvider.get(), viewModelCImpl.savedStateHandle);

          case 3: // com.example.vehiclemanager.core.ui.navigation.AppScaffoldViewModel 
          return (T) new AppScaffoldViewModel(singletonCImpl.formDirtyStateHolderProvider.get());

          case 4: // com.example.vehiclemanager.feature.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindFuelRecordRepositoryProvider.get(), singletonCImpl.bindMaintenanceScheduleRepositoryProvider.get(), new GetDashboardSummaryUseCase());

          case 5: // com.example.vehiclemanager.feature.fuel.FuelDetailViewModel 
          return (T) new FuelDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bindFuelRecordRepositoryProvider.get());

          case 6: // com.example.vehiclemanager.feature.fuel.FuelHistoryViewModel 
          return (T) new FuelHistoryViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindFuelRecordRepositoryProvider.get());

          case 7: // com.example.vehiclemanager.feature.maintenance.MaintenanceHistoryViewModel 
          return (T) new MaintenanceHistoryViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindMaintenanceRecordRepositoryProvider.get());

          case 8: // com.example.vehiclemanager.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.themePreferenceRepositoryImplProvider.get());

          case 9: // com.example.vehiclemanager.feature.statistics.StatisticsViewModel 
          return (T) new StatisticsViewModel(singletonCImpl.activeVehicleRepositoryImplProvider.get(), singletonCImpl.bindFuelRecordRepositoryProvider.get(), singletonCImpl.bindMaintenanceRecordRepositoryProvider.get(), new CalculateVehicleStatsUseCase());

          case 10: // com.example.vehiclemanager.feature.vehicles.VehicleBackupViewModel 
          return (T) new VehicleBackupViewModel(viewModelCImpl.exportDatabaseUseCase(), viewModelCImpl.importDatabaseUseCase(), singletonCImpl.bindVehicleRepositoryProvider.get(), singletonCImpl.activeVehicleRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends VehicleApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends VehicleApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends VehicleApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<ThemePreferenceRepositoryImpl> themePreferenceRepositoryImplProvider;

    private Provider<VehicleDatabase> provideVehicleDatabaseProvider;

    private Provider<VehicleDao> provideVehicleDaoProvider;

    private Provider<VehicleRepositoryImpl> vehicleRepositoryImplProvider;

    private Provider<VehicleRepository> bindVehicleRepositoryProvider;

    private Provider<FormDirtyStateHolder> formDirtyStateHolderProvider;

    private Provider<DataStore<Preferences>> provideActiveVehicleDataStoreProvider;

    private Provider<ActiveVehicleRepositoryImpl> activeVehicleRepositoryImplProvider;

    private Provider<FuelRecordDao> provideFuelRecordDaoProvider;

    private Provider<FuelRecordRepositoryImpl> fuelRecordRepositoryImplProvider;

    private Provider<FuelRecordRepository> bindFuelRecordRepositoryProvider;

    private Provider<MaintenanceRecordDao> provideMaintenanceRecordDaoProvider;

    private Provider<MaintenanceRecordRepositoryImpl> maintenanceRecordRepositoryImplProvider;

    private Provider<MaintenanceRecordRepository> bindMaintenanceRecordRepositoryProvider;

    private Provider<MaintenanceScheduleDao> provideMaintenanceScheduleDaoProvider;

    private Provider<MaintenanceScheduleRepositoryImpl> maintenanceScheduleRepositoryImplProvider;

    private Provider<MaintenanceScheduleRepository> bindMaintenanceScheduleRepositoryProvider;

    private Provider<RoomVehicleBackupRepository> roomVehicleBackupRepositoryProvider;

    private Provider<VehicleBackupRepository> bindVehicleBackupRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.themePreferenceRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ThemePreferenceRepositoryImpl>(singletonCImpl, 0));
      this.provideVehicleDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<VehicleDatabase>(singletonCImpl, 3));
      this.provideVehicleDaoProvider = DoubleCheck.provider(new SwitchingProvider<VehicleDao>(singletonCImpl, 2));
      this.vehicleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 1);
      this.bindVehicleRepositoryProvider = DoubleCheck.provider((Provider) vehicleRepositoryImplProvider);
      this.formDirtyStateHolderProvider = DoubleCheck.provider(new SwitchingProvider<FormDirtyStateHolder>(singletonCImpl, 4));
      this.provideActiveVehicleDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 6));
      this.activeVehicleRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ActiveVehicleRepositoryImpl>(singletonCImpl, 5));
      this.provideFuelRecordDaoProvider = DoubleCheck.provider(new SwitchingProvider<FuelRecordDao>(singletonCImpl, 8));
      this.fuelRecordRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.bindFuelRecordRepositoryProvider = DoubleCheck.provider((Provider) fuelRecordRepositoryImplProvider);
      this.provideMaintenanceRecordDaoProvider = DoubleCheck.provider(new SwitchingProvider<MaintenanceRecordDao>(singletonCImpl, 10));
      this.maintenanceRecordRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindMaintenanceRecordRepositoryProvider = DoubleCheck.provider((Provider) maintenanceRecordRepositoryImplProvider);
      this.provideMaintenanceScheduleDaoProvider = DoubleCheck.provider(new SwitchingProvider<MaintenanceScheduleDao>(singletonCImpl, 12));
      this.maintenanceScheduleRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.bindMaintenanceScheduleRepositoryProvider = DoubleCheck.provider((Provider) maintenanceScheduleRepositoryImplProvider);
      this.roomVehicleBackupRepositoryProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.bindVehicleBackupRepositoryProvider = DoubleCheck.provider((Provider) roomVehicleBackupRepositoryProvider);
    }

    @Override
    public void injectVehicleApplication(VehicleApplication vehicleApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.example.vehiclemanager.core.data.theme.ThemePreferenceRepositoryImpl 
          return (T) new ThemePreferenceRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.example.vehiclemanager.core.data.vehicle.VehicleRepositoryImpl 
          return (T) new VehicleRepositoryImpl(singletonCImpl.provideVehicleDaoProvider.get());

          case 2: // com.example.vehiclemanager.core.data.vehicle.VehicleDao 
          return (T) DatabaseModule_ProvideVehicleDaoFactory.provideVehicleDao(singletonCImpl.provideVehicleDatabaseProvider.get());

          case 3: // com.example.vehiclemanager.core.data.database.VehicleDatabase 
          return (T) DatabaseModule_ProvideVehicleDatabaseFactory.provideVehicleDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.example.vehiclemanager.core.ui.navigation.FormDirtyStateHolder 
          return (T) new FormDirtyStateHolder();

          case 5: // com.example.vehiclemanager.core.data.vehicle.ActiveVehicleRepositoryImpl 
          return (T) new ActiveVehicleRepositoryImpl(singletonCImpl.provideActiveVehicleDataStoreProvider.get(), singletonCImpl.bindVehicleRepositoryProvider.get());

          case 6: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) ActiveVehicleDataModule_Companion_ProvideActiveVehicleDataStoreFactory.provideActiveVehicleDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.example.vehiclemanager.core.data.fuel.FuelRecordRepositoryImpl 
          return (T) new FuelRecordRepositoryImpl(singletonCImpl.provideFuelRecordDaoProvider.get());

          case 8: // com.example.vehiclemanager.core.data.fuel.FuelRecordDao 
          return (T) DatabaseModule_ProvideFuelRecordDaoFactory.provideFuelRecordDao(singletonCImpl.provideVehicleDatabaseProvider.get());

          case 9: // com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordRepositoryImpl 
          return (T) new MaintenanceRecordRepositoryImpl(singletonCImpl.provideMaintenanceRecordDaoProvider.get());

          case 10: // com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordDao 
          return (T) DatabaseModule_ProvideMaintenanceRecordDaoFactory.provideMaintenanceRecordDao(singletonCImpl.provideVehicleDatabaseProvider.get());

          case 11: // com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleRepositoryImpl 
          return (T) new MaintenanceScheduleRepositoryImpl(singletonCImpl.provideMaintenanceScheduleDaoProvider.get(), singletonCImpl.activeVehicleRepositoryImplProvider.get());

          case 12: // com.example.vehiclemanager.core.data.maintenance.MaintenanceScheduleDao 
          return (T) DatabaseModule_ProvideMaintenanceScheduleDaoFactory.provideMaintenanceScheduleDao(singletonCImpl.provideVehicleDatabaseProvider.get());

          case 13: // com.example.vehiclemanager.core.data.database.RoomVehicleBackupRepository 
          return (T) new RoomVehicleBackupRepository(singletonCImpl.provideVehicleDatabaseProvider.get(), singletonCImpl.provideVehicleDaoProvider.get(), singletonCImpl.provideFuelRecordDaoProvider.get(), singletonCImpl.provideMaintenanceRecordDaoProvider.get(), singletonCImpl.provideMaintenanceScheduleDaoProvider.get(), singletonCImpl.activeVehicleRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
