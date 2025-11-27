package com.qaassist.inspection.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.qaassist.inspection.database.entities.InspectionEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InspectionDao_Impl implements InspectionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InspectionEntity> __insertionAdapterOfInspectionEntity;

  private final EntityDeletionOrUpdateAdapter<InspectionEntity> __deletionAdapterOfInspectionEntity;

  private final EntityDeletionOrUpdateAdapter<InspectionEntity> __updateAdapterOfInspectionEntity;

  public InspectionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInspectionEntity = new EntityInsertionAdapter<InspectionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `inspections` (`id`,`date`,`project`,`municipality`,`olt`,`fsa`,`asBuilt`,`inspectionType`,`equipmentId`,`address`,`drawing`,`observations`,`latitude`,`longitude`,`excelPath`,`excelUri`,`photosPaths`,`createdTimestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InspectionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getProject());
        statement.bindString(4, entity.getMunicipality());
        statement.bindString(5, entity.getOlt());
        statement.bindString(6, entity.getFsa());
        statement.bindString(7, entity.getAsBuilt());
        statement.bindString(8, entity.getInspectionType());
        statement.bindString(9, entity.getEquipmentId());
        statement.bindString(10, entity.getAddress());
        statement.bindString(11, entity.getDrawing());
        statement.bindString(12, entity.getObservations());
        if (entity.getLatitude() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getLongitude());
        }
        statement.bindString(15, entity.getExcelPath());
        statement.bindString(16, entity.getExcelUri());
        statement.bindString(17, entity.getPhotosPaths());
        statement.bindLong(18, entity.getCreatedTimestamp());
      }
    };
    this.__deletionAdapterOfInspectionEntity = new EntityDeletionOrUpdateAdapter<InspectionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `inspections` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InspectionEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfInspectionEntity = new EntityDeletionOrUpdateAdapter<InspectionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `inspections` SET `id` = ?,`date` = ?,`project` = ?,`municipality` = ?,`olt` = ?,`fsa` = ?,`asBuilt` = ?,`inspectionType` = ?,`equipmentId` = ?,`address` = ?,`drawing` = ?,`observations` = ?,`latitude` = ?,`longitude` = ?,`excelPath` = ?,`excelUri` = ?,`photosPaths` = ?,`createdTimestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InspectionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getProject());
        statement.bindString(4, entity.getMunicipality());
        statement.bindString(5, entity.getOlt());
        statement.bindString(6, entity.getFsa());
        statement.bindString(7, entity.getAsBuilt());
        statement.bindString(8, entity.getInspectionType());
        statement.bindString(9, entity.getEquipmentId());
        statement.bindString(10, entity.getAddress());
        statement.bindString(11, entity.getDrawing());
        statement.bindString(12, entity.getObservations());
        if (entity.getLatitude() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getLongitude());
        }
        statement.bindString(15, entity.getExcelPath());
        statement.bindString(16, entity.getExcelUri());
        statement.bindString(17, entity.getPhotosPaths());
        statement.bindLong(18, entity.getCreatedTimestamp());
        statement.bindLong(19, entity.getId());
      }
    };
  }

  @Override
  public Object insertInspection(final InspectionEntity inspection,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfInspectionEntity.insertAndReturnId(inspection);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInspection(final InspectionEntity inspection,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInspectionEntity.handle(inspection);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateInspection(final InspectionEntity inspection,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfInspectionEntity.handle(inspection);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllInspections(final Continuation<? super List<InspectionEntity>> $completion) {
    final String _sql = "SELECT * FROM inspections ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InspectionEntity>>() {
      @Override
      @NonNull
      public List<InspectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfProject = CursorUtil.getColumnIndexOrThrow(_cursor, "project");
          final int _cursorIndexOfMunicipality = CursorUtil.getColumnIndexOrThrow(_cursor, "municipality");
          final int _cursorIndexOfOlt = CursorUtil.getColumnIndexOrThrow(_cursor, "olt");
          final int _cursorIndexOfFsa = CursorUtil.getColumnIndexOrThrow(_cursor, "fsa");
          final int _cursorIndexOfAsBuilt = CursorUtil.getColumnIndexOrThrow(_cursor, "asBuilt");
          final int _cursorIndexOfInspectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inspectionType");
          final int _cursorIndexOfEquipmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipmentId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfDrawing = CursorUtil.getColumnIndexOrThrow(_cursor, "drawing");
          final int _cursorIndexOfObservations = CursorUtil.getColumnIndexOrThrow(_cursor, "observations");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfExcelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "excelPath");
          final int _cursorIndexOfExcelUri = CursorUtil.getColumnIndexOrThrow(_cursor, "excelUri");
          final int _cursorIndexOfPhotosPaths = CursorUtil.getColumnIndexOrThrow(_cursor, "photosPaths");
          final int _cursorIndexOfCreatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTimestamp");
          final List<InspectionEntity> _result = new ArrayList<InspectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InspectionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpProject;
            _tmpProject = _cursor.getString(_cursorIndexOfProject);
            final String _tmpMunicipality;
            _tmpMunicipality = _cursor.getString(_cursorIndexOfMunicipality);
            final String _tmpOlt;
            _tmpOlt = _cursor.getString(_cursorIndexOfOlt);
            final String _tmpFsa;
            _tmpFsa = _cursor.getString(_cursorIndexOfFsa);
            final String _tmpAsBuilt;
            _tmpAsBuilt = _cursor.getString(_cursorIndexOfAsBuilt);
            final String _tmpInspectionType;
            _tmpInspectionType = _cursor.getString(_cursorIndexOfInspectionType);
            final String _tmpEquipmentId;
            _tmpEquipmentId = _cursor.getString(_cursorIndexOfEquipmentId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpDrawing;
            _tmpDrawing = _cursor.getString(_cursorIndexOfDrawing);
            final String _tmpObservations;
            _tmpObservations = _cursor.getString(_cursorIndexOfObservations);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpExcelPath;
            _tmpExcelPath = _cursor.getString(_cursorIndexOfExcelPath);
            final String _tmpExcelUri;
            _tmpExcelUri = _cursor.getString(_cursorIndexOfExcelUri);
            final String _tmpPhotosPaths;
            _tmpPhotosPaths = _cursor.getString(_cursorIndexOfPhotosPaths);
            final long _tmpCreatedTimestamp;
            _tmpCreatedTimestamp = _cursor.getLong(_cursorIndexOfCreatedTimestamp);
            _item = new InspectionEntity(_tmpId,_tmpDate,_tmpProject,_tmpMunicipality,_tmpOlt,_tmpFsa,_tmpAsBuilt,_tmpInspectionType,_tmpEquipmentId,_tmpAddress,_tmpDrawing,_tmpObservations,_tmpLatitude,_tmpLongitude,_tmpExcelPath,_tmpExcelUri,_tmpPhotosPaths,_tmpCreatedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getInspectionById(final long id,
      final Continuation<? super InspectionEntity> $completion) {
    final String _sql = "SELECT * FROM inspections WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InspectionEntity>() {
      @Override
      @Nullable
      public InspectionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfProject = CursorUtil.getColumnIndexOrThrow(_cursor, "project");
          final int _cursorIndexOfMunicipality = CursorUtil.getColumnIndexOrThrow(_cursor, "municipality");
          final int _cursorIndexOfOlt = CursorUtil.getColumnIndexOrThrow(_cursor, "olt");
          final int _cursorIndexOfFsa = CursorUtil.getColumnIndexOrThrow(_cursor, "fsa");
          final int _cursorIndexOfAsBuilt = CursorUtil.getColumnIndexOrThrow(_cursor, "asBuilt");
          final int _cursorIndexOfInspectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inspectionType");
          final int _cursorIndexOfEquipmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipmentId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfDrawing = CursorUtil.getColumnIndexOrThrow(_cursor, "drawing");
          final int _cursorIndexOfObservations = CursorUtil.getColumnIndexOrThrow(_cursor, "observations");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfExcelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "excelPath");
          final int _cursorIndexOfExcelUri = CursorUtil.getColumnIndexOrThrow(_cursor, "excelUri");
          final int _cursorIndexOfPhotosPaths = CursorUtil.getColumnIndexOrThrow(_cursor, "photosPaths");
          final int _cursorIndexOfCreatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTimestamp");
          final InspectionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpProject;
            _tmpProject = _cursor.getString(_cursorIndexOfProject);
            final String _tmpMunicipality;
            _tmpMunicipality = _cursor.getString(_cursorIndexOfMunicipality);
            final String _tmpOlt;
            _tmpOlt = _cursor.getString(_cursorIndexOfOlt);
            final String _tmpFsa;
            _tmpFsa = _cursor.getString(_cursorIndexOfFsa);
            final String _tmpAsBuilt;
            _tmpAsBuilt = _cursor.getString(_cursorIndexOfAsBuilt);
            final String _tmpInspectionType;
            _tmpInspectionType = _cursor.getString(_cursorIndexOfInspectionType);
            final String _tmpEquipmentId;
            _tmpEquipmentId = _cursor.getString(_cursorIndexOfEquipmentId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpDrawing;
            _tmpDrawing = _cursor.getString(_cursorIndexOfDrawing);
            final String _tmpObservations;
            _tmpObservations = _cursor.getString(_cursorIndexOfObservations);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpExcelPath;
            _tmpExcelPath = _cursor.getString(_cursorIndexOfExcelPath);
            final String _tmpExcelUri;
            _tmpExcelUri = _cursor.getString(_cursorIndexOfExcelUri);
            final String _tmpPhotosPaths;
            _tmpPhotosPaths = _cursor.getString(_cursorIndexOfPhotosPaths);
            final long _tmpCreatedTimestamp;
            _tmpCreatedTimestamp = _cursor.getLong(_cursorIndexOfCreatedTimestamp);
            _result = new InspectionEntity(_tmpId,_tmpDate,_tmpProject,_tmpMunicipality,_tmpOlt,_tmpFsa,_tmpAsBuilt,_tmpInspectionType,_tmpEquipmentId,_tmpAddress,_tmpDrawing,_tmpObservations,_tmpLatitude,_tmpLongitude,_tmpExcelPath,_tmpExcelUri,_tmpPhotosPaths,_tmpCreatedTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getInspectionsByProject(final String project,
      final Continuation<? super List<InspectionEntity>> $completion) {
    final String _sql = "SELECT * FROM inspections WHERE project = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, project);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InspectionEntity>>() {
      @Override
      @NonNull
      public List<InspectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfProject = CursorUtil.getColumnIndexOrThrow(_cursor, "project");
          final int _cursorIndexOfMunicipality = CursorUtil.getColumnIndexOrThrow(_cursor, "municipality");
          final int _cursorIndexOfOlt = CursorUtil.getColumnIndexOrThrow(_cursor, "olt");
          final int _cursorIndexOfFsa = CursorUtil.getColumnIndexOrThrow(_cursor, "fsa");
          final int _cursorIndexOfAsBuilt = CursorUtil.getColumnIndexOrThrow(_cursor, "asBuilt");
          final int _cursorIndexOfInspectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inspectionType");
          final int _cursorIndexOfEquipmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipmentId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfDrawing = CursorUtil.getColumnIndexOrThrow(_cursor, "drawing");
          final int _cursorIndexOfObservations = CursorUtil.getColumnIndexOrThrow(_cursor, "observations");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfExcelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "excelPath");
          final int _cursorIndexOfExcelUri = CursorUtil.getColumnIndexOrThrow(_cursor, "excelUri");
          final int _cursorIndexOfPhotosPaths = CursorUtil.getColumnIndexOrThrow(_cursor, "photosPaths");
          final int _cursorIndexOfCreatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTimestamp");
          final List<InspectionEntity> _result = new ArrayList<InspectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InspectionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpProject;
            _tmpProject = _cursor.getString(_cursorIndexOfProject);
            final String _tmpMunicipality;
            _tmpMunicipality = _cursor.getString(_cursorIndexOfMunicipality);
            final String _tmpOlt;
            _tmpOlt = _cursor.getString(_cursorIndexOfOlt);
            final String _tmpFsa;
            _tmpFsa = _cursor.getString(_cursorIndexOfFsa);
            final String _tmpAsBuilt;
            _tmpAsBuilt = _cursor.getString(_cursorIndexOfAsBuilt);
            final String _tmpInspectionType;
            _tmpInspectionType = _cursor.getString(_cursorIndexOfInspectionType);
            final String _tmpEquipmentId;
            _tmpEquipmentId = _cursor.getString(_cursorIndexOfEquipmentId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpDrawing;
            _tmpDrawing = _cursor.getString(_cursorIndexOfDrawing);
            final String _tmpObservations;
            _tmpObservations = _cursor.getString(_cursorIndexOfObservations);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpExcelPath;
            _tmpExcelPath = _cursor.getString(_cursorIndexOfExcelPath);
            final String _tmpExcelUri;
            _tmpExcelUri = _cursor.getString(_cursorIndexOfExcelUri);
            final String _tmpPhotosPaths;
            _tmpPhotosPaths = _cursor.getString(_cursorIndexOfPhotosPaths);
            final long _tmpCreatedTimestamp;
            _tmpCreatedTimestamp = _cursor.getLong(_cursorIndexOfCreatedTimestamp);
            _item = new InspectionEntity(_tmpId,_tmpDate,_tmpProject,_tmpMunicipality,_tmpOlt,_tmpFsa,_tmpAsBuilt,_tmpInspectionType,_tmpEquipmentId,_tmpAddress,_tmpDrawing,_tmpObservations,_tmpLatitude,_tmpLongitude,_tmpExcelPath,_tmpExcelUri,_tmpPhotosPaths,_tmpCreatedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getInspectionsByType(final String type,
      final Continuation<? super List<InspectionEntity>> $completion) {
    final String _sql = "SELECT * FROM inspections WHERE inspectionType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InspectionEntity>>() {
      @Override
      @NonNull
      public List<InspectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfProject = CursorUtil.getColumnIndexOrThrow(_cursor, "project");
          final int _cursorIndexOfMunicipality = CursorUtil.getColumnIndexOrThrow(_cursor, "municipality");
          final int _cursorIndexOfOlt = CursorUtil.getColumnIndexOrThrow(_cursor, "olt");
          final int _cursorIndexOfFsa = CursorUtil.getColumnIndexOrThrow(_cursor, "fsa");
          final int _cursorIndexOfAsBuilt = CursorUtil.getColumnIndexOrThrow(_cursor, "asBuilt");
          final int _cursorIndexOfInspectionType = CursorUtil.getColumnIndexOrThrow(_cursor, "inspectionType");
          final int _cursorIndexOfEquipmentId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipmentId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfDrawing = CursorUtil.getColumnIndexOrThrow(_cursor, "drawing");
          final int _cursorIndexOfObservations = CursorUtil.getColumnIndexOrThrow(_cursor, "observations");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfExcelPath = CursorUtil.getColumnIndexOrThrow(_cursor, "excelPath");
          final int _cursorIndexOfExcelUri = CursorUtil.getColumnIndexOrThrow(_cursor, "excelUri");
          final int _cursorIndexOfPhotosPaths = CursorUtil.getColumnIndexOrThrow(_cursor, "photosPaths");
          final int _cursorIndexOfCreatedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTimestamp");
          final List<InspectionEntity> _result = new ArrayList<InspectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InspectionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpProject;
            _tmpProject = _cursor.getString(_cursorIndexOfProject);
            final String _tmpMunicipality;
            _tmpMunicipality = _cursor.getString(_cursorIndexOfMunicipality);
            final String _tmpOlt;
            _tmpOlt = _cursor.getString(_cursorIndexOfOlt);
            final String _tmpFsa;
            _tmpFsa = _cursor.getString(_cursorIndexOfFsa);
            final String _tmpAsBuilt;
            _tmpAsBuilt = _cursor.getString(_cursorIndexOfAsBuilt);
            final String _tmpInspectionType;
            _tmpInspectionType = _cursor.getString(_cursorIndexOfInspectionType);
            final String _tmpEquipmentId;
            _tmpEquipmentId = _cursor.getString(_cursorIndexOfEquipmentId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpDrawing;
            _tmpDrawing = _cursor.getString(_cursorIndexOfDrawing);
            final String _tmpObservations;
            _tmpObservations = _cursor.getString(_cursorIndexOfObservations);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpExcelPath;
            _tmpExcelPath = _cursor.getString(_cursorIndexOfExcelPath);
            final String _tmpExcelUri;
            _tmpExcelUri = _cursor.getString(_cursorIndexOfExcelUri);
            final String _tmpPhotosPaths;
            _tmpPhotosPaths = _cursor.getString(_cursorIndexOfPhotosPaths);
            final long _tmpCreatedTimestamp;
            _tmpCreatedTimestamp = _cursor.getLong(_cursorIndexOfCreatedTimestamp);
            _item = new InspectionEntity(_tmpId,_tmpDate,_tmpProject,_tmpMunicipality,_tmpOlt,_tmpFsa,_tmpAsBuilt,_tmpInspectionType,_tmpEquipmentId,_tmpAddress,_tmpDrawing,_tmpObservations,_tmpLatitude,_tmpLongitude,_tmpExcelPath,_tmpExcelUri,_tmpPhotosPaths,_tmpCreatedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getInspectionCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM inspections";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInspections(final Set<Long> inspectionIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM inspections WHERE id IN (");
        final int _inputSize = inspectionIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : inspectionIds) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
