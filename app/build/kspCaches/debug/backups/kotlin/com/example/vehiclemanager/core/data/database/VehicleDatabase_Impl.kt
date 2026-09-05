package com.example.vehiclemanager.core.`data`.database

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.vehiclemanager.core.`data`.fuel.FuelRecordDao
import com.example.vehiclemanager.core.`data`.fuel.FuelRecordDao_Impl
import com.example.vehiclemanager.core.`data`.maintenance.MaintenanceRecordDao
import com.example.vehiclemanager.core.`data`.maintenance.MaintenanceRecordDao_Impl
import com.example.vehiclemanager.core.`data`.vehicle.VehicleDao
import com.example.vehiclemanager.core.`data`.vehicle.VehicleDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Boolean
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class VehicleDatabase_Impl : VehicleDatabase() {
  private val _vehicleDao: Lazy<VehicleDao> = lazy {
    VehicleDao_Impl(this)
  }


  private val _fuelRecordDao: Lazy<FuelRecordDao> = lazy {
    FuelRecordDao_Impl(this)
  }


  private val _maintenanceRecordDao: Lazy<MaintenanceRecordDao> = lazy {
    MaintenanceRecordDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(3) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `make` TEXT NOT NULL, `model` TEXT NOT NULL, `year` INTEGER NOT NULL, `licensePlate` TEXT NOT NULL, `vin` TEXT, `fuelType` TEXT NOT NULL, `primaryOdometerKm` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `fuel_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `timestampMs` INTEGER NOT NULL, `odometerKm` INTEGER NOT NULL, `litersX100` INTEGER NOT NULL, `pricePerLiterCents` INTEGER NOT NULL, `totalCostCents` INTEGER NOT NULL, `isFullTank` INTEGER NOT NULL, `stationName` TEXT, `notes` TEXT, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fuel_records_vehicleId_timestampMs` ON `fuel_records` (`vehicleId`, `timestampMs`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `maintenance_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `costCents` INTEGER NOT NULL, `odometerKm` INTEGER NOT NULL, `timestampMs` INTEGER NOT NULL, `notes` TEXT, `performedBy` TEXT, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_maintenance_records_vehicleId_timestampMs` ON `maintenance_records` (`vehicleId`, `timestampMs`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9c93b5732b90d575338d04bbb7a890c6')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `vehicles`")
        db.execSQL("DROP TABLE IF EXISTS `fuel_records`")
        db.execSQL("DROP TABLE IF EXISTS `maintenance_records`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        db.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsVehicles: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsVehicles.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("make", TableInfo.Column("make", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("model", TableInfo.Column("model", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("year", TableInfo.Column("year", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("licensePlate", TableInfo.Column("licensePlate", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("vin", TableInfo.Column("vin", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("fuelType", TableInfo.Column("fuelType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVehicles.put("primaryOdometerKm", TableInfo.Column("primaryOdometerKm", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVehicles: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesVehicles: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoVehicles: TableInfo = TableInfo("vehicles", _columnsVehicles, _foreignKeysVehicles,
            _indicesVehicles)
        val _existingVehicles: TableInfo = read(db, "vehicles")
        if (!_infoVehicles.equals(_existingVehicles)) {
          return RoomOpenHelper.ValidationResult(false, """
              |vehicles(com.example.vehiclemanager.core.data.vehicle.VehicleEntity).
              | Expected:
              |""".trimMargin() + _infoVehicles + """
              |
              | Found:
              |""".trimMargin() + _existingVehicles)
        }
        val _columnsFuelRecords: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(10)
        _columnsFuelRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("vehicleId", TableInfo.Column("vehicleId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("timestampMs", TableInfo.Column("timestampMs", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("odometerKm", TableInfo.Column("odometerKm", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("litersX100", TableInfo.Column("litersX100", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("pricePerLiterCents", TableInfo.Column("pricePerLiterCents",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("totalCostCents", TableInfo.Column("totalCostCents", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("isFullTank", TableInfo.Column("isFullTank", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("stationName", TableInfo.Column("stationName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFuelRecords.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFuelRecords: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysFuelRecords.add(TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION",
            listOf("vehicleId"), listOf("id")))
        val _indicesFuelRecords: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesFuelRecords.add(TableInfo.Index("index_fuel_records_vehicleId_timestampMs", false,
            listOf("vehicleId", "timestampMs"), listOf("ASC", "ASC")))
        val _infoFuelRecords: TableInfo = TableInfo("fuel_records", _columnsFuelRecords,
            _foreignKeysFuelRecords, _indicesFuelRecords)
        val _existingFuelRecords: TableInfo = read(db, "fuel_records")
        if (!_infoFuelRecords.equals(_existingFuelRecords)) {
          return RoomOpenHelper.ValidationResult(false, """
              |fuel_records(com.example.vehiclemanager.core.data.fuel.FuelRecordEntity).
              | Expected:
              |""".trimMargin() + _infoFuelRecords + """
              |
              | Found:
              |""".trimMargin() + _existingFuelRecords)
        }
        val _columnsMaintenanceRecords: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsMaintenanceRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("vehicleId", TableInfo.Column("vehicleId", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("category", TableInfo.Column("category", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("costCents", TableInfo.Column("costCents", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("odometerKm", TableInfo.Column("odometerKm", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("timestampMs", TableInfo.Column("timestampMs", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMaintenanceRecords.put("performedBy", TableInfo.Column("performedBy", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMaintenanceRecords: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysMaintenanceRecords.add(TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION",
            listOf("vehicleId"), listOf("id")))
        val _indicesMaintenanceRecords: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(1)
        _indicesMaintenanceRecords.add(TableInfo.Index("index_maintenance_records_vehicleId_timestampMs",
            false, listOf("vehicleId", "timestampMs"), listOf("ASC", "ASC")))
        val _infoMaintenanceRecords: TableInfo = TableInfo("maintenance_records",
            _columnsMaintenanceRecords, _foreignKeysMaintenanceRecords, _indicesMaintenanceRecords)
        val _existingMaintenanceRecords: TableInfo = read(db, "maintenance_records")
        if (!_infoMaintenanceRecords.equals(_existingMaintenanceRecords)) {
          return RoomOpenHelper.ValidationResult(false, """
              |maintenance_records(com.example.vehiclemanager.core.data.maintenance.MaintenanceRecordEntity).
              | Expected:
              |""".trimMargin() + _infoMaintenanceRecords + """
              |
              | Found:
              |""".trimMargin() + _existingMaintenanceRecords)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "9c93b5732b90d575338d04bbb7a890c6", "8c7d418e20c9569f571fcce14d3bb832")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "vehicles","fuel_records","maintenance_records")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    val _supportsDeferForeignKeys: Boolean = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.LOLLIPOP
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE")
      }
      super.beginTransaction()
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE")
      }
      _db.execSQL("DELETE FROM `vehicles`")
      _db.execSQL("DELETE FROM `fuel_records`")
      _db.execSQL("DELETE FROM `maintenance_records`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE")
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(VehicleDao::class.java, VehicleDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FuelRecordDao::class.java, FuelRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MaintenanceRecordDao::class.java,
        MaintenanceRecordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun vehicleDao(): VehicleDao = _vehicleDao.value

  public override fun fuelRecordDao(): FuelRecordDao = _fuelRecordDao.value

  public override fun maintenanceRecordDao(): MaintenanceRecordDao = _maintenanceRecordDao.value
}
