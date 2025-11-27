package com.qaassist.inspection.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.qaassist.inspection.database.dao.InspectionDao;
import com.qaassist.inspection.database.dao.InspectionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile InspectionDao _inspectionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `inspections` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `project` TEXT NOT NULL, `municipality` TEXT NOT NULL, `olt` TEXT NOT NULL, `fsa` TEXT NOT NULL, `asBuilt` TEXT NOT NULL, `inspectionType` TEXT NOT NULL, `equipmentId` TEXT NOT NULL, `address` TEXT NOT NULL, `drawing` TEXT NOT NULL, `observations` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `excelPath` TEXT NOT NULL, `excelUri` TEXT NOT NULL, `photosPaths` TEXT NOT NULL, `createdTimestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '228b90548edd4fd9c3d6ec0093f4c626')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `inspections`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsInspections = new HashMap<String, TableInfo.Column>(18);
        _columnsInspections.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("project", new TableInfo.Column("project", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("municipality", new TableInfo.Column("municipality", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("olt", new TableInfo.Column("olt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("fsa", new TableInfo.Column("fsa", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("asBuilt", new TableInfo.Column("asBuilt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("inspectionType", new TableInfo.Column("inspectionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("equipmentId", new TableInfo.Column("equipmentId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("drawing", new TableInfo.Column("drawing", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("observations", new TableInfo.Column("observations", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("excelPath", new TableInfo.Column("excelPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("excelUri", new TableInfo.Column("excelUri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("photosPaths", new TableInfo.Column("photosPaths", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInspections.put("createdTimestamp", new TableInfo.Column("createdTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInspections = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesInspections = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoInspections = new TableInfo("inspections", _columnsInspections, _foreignKeysInspections, _indicesInspections);
        final TableInfo _existingInspections = TableInfo.read(db, "inspections");
        if (!_infoInspections.equals(_existingInspections)) {
          return new RoomOpenHelper.ValidationResult(false, "inspections(com.qaassist.inspection.database.entities.InspectionEntity).\n"
                  + " Expected:\n" + _infoInspections + "\n"
                  + " Found:\n" + _existingInspections);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "228b90548edd4fd9c3d6ec0093f4c626", "9b3a1ad6b9b9faba66004ce324880e6e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "inspections");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `inspections`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(InspectionDao.class, InspectionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public InspectionDao inspectionDao() {
    if (_inspectionDao != null) {
      return _inspectionDao;
    } else {
      synchronized(this) {
        if(_inspectionDao == null) {
          _inspectionDao = new InspectionDao_Impl(this);
        }
        return _inspectionDao;
      }
    }
  }
}
