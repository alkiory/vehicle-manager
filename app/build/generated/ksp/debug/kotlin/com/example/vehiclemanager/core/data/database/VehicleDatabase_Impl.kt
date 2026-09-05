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
import com.example.vehiclemanager.core.`data`.vehicle.VehicleDao
import com.example.vehiclemanager.core.`data`.vehicle.VehicleDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
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


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(1) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `make` TEXT NOT NULL, `model` TEXT NOT NULL, `year` INTEGER NOT NULL, `licensePlate` TEXT NOT NULL, `vin` TEXT, `fuelType` TEXT NOT NULL, `primaryOdometerKm` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f0558f124fee0bdb7e335d1fde66b47f')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `vehicles`")
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
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "f0558f124fee0bdb7e335d1fde66b47f", "80de93acf5bdfe73ff1f8f7864baf7e9")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "vehicles")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    try {
      super.beginTransaction()
      _db.execSQL("DELETE FROM `vehicles`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
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
}
