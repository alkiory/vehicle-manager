package com.example.vehiclemanager.core.`data`.vehicle

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.vehiclemanager.core.`data`.database.Converters
import com.example.vehiclemanager.core.domain.FuelType
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class VehicleDao_Impl(
  __db: RoomDatabase,
) : VehicleDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfVehicleEntity: EntityInsertionAdapter<VehicleEntity>

  private val __converters: Converters = Converters()

  private val __deletionAdapterOfVehicleEntity: EntityDeletionOrUpdateAdapter<VehicleEntity>

  private val __updateAdapterOfVehicleEntity: EntityDeletionOrUpdateAdapter<VehicleEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfVehicleEntity = object : EntityInsertionAdapter<VehicleEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `vehicles` (`id`,`name`,`make`,`model`,`year`,`licensePlate`,`vin`,`fuelType`,`primaryOdometerKm`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VehicleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindString(3, entity.make)
        statement.bindString(4, entity.model)
        statement.bindLong(5, entity.year.toLong())
        statement.bindString(6, entity.licensePlate)
        val _tmpVin: String? = entity.vin
        if (_tmpVin == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpVin)
        }
        val _tmp: String? = __converters.fuelTypeToName(entity.fuelType)
        if (_tmp == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmp)
        }
        statement.bindLong(9, entity.primaryOdometerKm)
      }
    }
    this.__deletionAdapterOfVehicleEntity = object :
        EntityDeletionOrUpdateAdapter<VehicleEntity>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `vehicles` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VehicleEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfVehicleEntity = object :
        EntityDeletionOrUpdateAdapter<VehicleEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `vehicles` SET `id` = ?,`name` = ?,`make` = ?,`model` = ?,`year` = ?,`licensePlate` = ?,`vin` = ?,`fuelType` = ?,`primaryOdometerKm` = ? WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VehicleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindString(3, entity.make)
        statement.bindString(4, entity.model)
        statement.bindLong(5, entity.year.toLong())
        statement.bindString(6, entity.licensePlate)
        val _tmpVin: String? = entity.vin
        if (_tmpVin == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpVin)
        }
        val _tmp: String? = __converters.fuelTypeToName(entity.fuelType)
        if (_tmp == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmp)
        }
        statement.bindLong(9, entity.primaryOdometerKm)
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insert(vehicle: VehicleEntity): Long = CoroutinesRoom.execute(__db,
      true, object : Callable<Long> {
    public override fun call(): Long {
      __db.beginTransaction()
      try {
        val _result: Long = __insertionAdapterOfVehicleEntity.insertAndReturnId(vehicle)
        __db.setTransactionSuccessful()
        return _result
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun delete(vehicle: VehicleEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfVehicleEntity.handle(vehicle)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun update(vehicle: VehicleEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfVehicleEntity.handle(vehicle)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun observeAll(): Flow<List<VehicleEntity>> {
    val _sql: String = "SELECT * FROM vehicles ORDER BY id ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("vehicles"), object :
        Callable<List<VehicleEntity>> {
      public override fun call(): List<VehicleEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfMake: Int = getColumnIndexOrThrow(_cursor, "make")
          val _cursorIndexOfModel: Int = getColumnIndexOrThrow(_cursor, "model")
          val _cursorIndexOfYear: Int = getColumnIndexOrThrow(_cursor, "year")
          val _cursorIndexOfLicensePlate: Int = getColumnIndexOrThrow(_cursor, "licensePlate")
          val _cursorIndexOfVin: Int = getColumnIndexOrThrow(_cursor, "vin")
          val _cursorIndexOfFuelType: Int = getColumnIndexOrThrow(_cursor, "fuelType")
          val _cursorIndexOfPrimaryOdometerKm: Int = getColumnIndexOrThrow(_cursor,
              "primaryOdometerKm")
          val _result: MutableList<VehicleEntity> = ArrayList<VehicleEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VehicleEntity
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpMake: String
            _tmpMake = _cursor.getString(_cursorIndexOfMake)
            val _tmpModel: String
            _tmpModel = _cursor.getString(_cursorIndexOfModel)
            val _tmpYear: Int
            _tmpYear = _cursor.getInt(_cursorIndexOfYear)
            val _tmpLicensePlate: String
            _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate)
            val _tmpVin: String?
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin)
            }
            val _tmpFuelType: FuelType
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfFuelType)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFuelType)
            }
            val _tmp_1: FuelType? = __converters.nameToFuelType(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.vehiclemanager.core.domain.FuelType', but it was NULL.")
            } else {
              _tmpFuelType = _tmp_1
            }
            val _tmpPrimaryOdometerKm: Long
            _tmpPrimaryOdometerKm = _cursor.getLong(_cursorIndexOfPrimaryOdometerKm)
            _item =
                VehicleEntity(_tmpId,_tmpName,_tmpMake,_tmpModel,_tmpYear,_tmpLicensePlate,_tmpVin,_tmpFuelType,_tmpPrimaryOdometerKm)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override suspend fun findById(id: Long): VehicleEntity? {
    val _sql: String = "SELECT * FROM vehicles WHERE id = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, id)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<VehicleEntity?> {
      public override fun call(): VehicleEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfMake: Int = getColumnIndexOrThrow(_cursor, "make")
          val _cursorIndexOfModel: Int = getColumnIndexOrThrow(_cursor, "model")
          val _cursorIndexOfYear: Int = getColumnIndexOrThrow(_cursor, "year")
          val _cursorIndexOfLicensePlate: Int = getColumnIndexOrThrow(_cursor, "licensePlate")
          val _cursorIndexOfVin: Int = getColumnIndexOrThrow(_cursor, "vin")
          val _cursorIndexOfFuelType: Int = getColumnIndexOrThrow(_cursor, "fuelType")
          val _cursorIndexOfPrimaryOdometerKm: Int = getColumnIndexOrThrow(_cursor,
              "primaryOdometerKm")
          val _result: VehicleEntity?
          if (_cursor.moveToFirst()) {
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpMake: String
            _tmpMake = _cursor.getString(_cursorIndexOfMake)
            val _tmpModel: String
            _tmpModel = _cursor.getString(_cursorIndexOfModel)
            val _tmpYear: Int
            _tmpYear = _cursor.getInt(_cursorIndexOfYear)
            val _tmpLicensePlate: String
            _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate)
            val _tmpVin: String?
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin)
            }
            val _tmpFuelType: FuelType
            val _tmp: String?
            if (_cursor.isNull(_cursorIndexOfFuelType)) {
              _tmp = null
            } else {
              _tmp = _cursor.getString(_cursorIndexOfFuelType)
            }
            val _tmp_1: FuelType? = __converters.nameToFuelType(_tmp)
            if (_tmp_1 == null) {
              error("Expected NON-NULL 'com.example.vehiclemanager.core.domain.FuelType', but it was NULL.")
            } else {
              _tmpFuelType = _tmp_1
            }
            val _tmpPrimaryOdometerKm: Long
            _tmpPrimaryOdometerKm = _cursor.getLong(_cursorIndexOfPrimaryOdometerKm)
            _result =
                VehicleEntity(_tmpId,_tmpName,_tmpMake,_tmpModel,_tmpYear,_tmpLicensePlate,_tmpVin,_tmpFuelType,_tmpPrimaryOdometerKm)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
