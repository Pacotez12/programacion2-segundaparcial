package com.uninorte.SegundoParcial.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.uninorte.SegundoParcial.data.entity.LogApp;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LogDao_Impl implements LogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LogApp> __insertionAdapterOfLogApp;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public LogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLogApp = new EntityInsertionAdapter<LogApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `logs_app` (`id`,`fechaHora`,`descripcionError`,`claseOrigen`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final LogApp entity) {
        statement.bindLong(1, entity.id);
        if (entity.fechaHora == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.fechaHora);
        }
        if (entity.descripcionError == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.descripcionError);
        }
        if (entity.claseOrigen == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.claseOrigen);
        }
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM logs_app";
        return _query;
      }
    };
  }

  @Override
  public long insert(final LogApp log) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfLogApp.insertAndReturnId(log);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAll.release(_stmt);
    }
  }

  @Override
  public List<LogApp> getAll() {
    final String _sql = "SELECT * FROM logs_app ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfFechaHora = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaHora");
      final int _cursorIndexOfDescripcionError = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcionError");
      final int _cursorIndexOfClaseOrigen = CursorUtil.getColumnIndexOrThrow(_cursor, "claseOrigen");
      final List<LogApp> _result = new ArrayList<LogApp>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final LogApp _item;
        final String _tmpFechaHora;
        if (_cursor.isNull(_cursorIndexOfFechaHora)) {
          _tmpFechaHora = null;
        } else {
          _tmpFechaHora = _cursor.getString(_cursorIndexOfFechaHora);
        }
        final String _tmpDescripcionError;
        if (_cursor.isNull(_cursorIndexOfDescripcionError)) {
          _tmpDescripcionError = null;
        } else {
          _tmpDescripcionError = _cursor.getString(_cursorIndexOfDescripcionError);
        }
        final String _tmpClaseOrigen;
        if (_cursor.isNull(_cursorIndexOfClaseOrigen)) {
          _tmpClaseOrigen = null;
        } else {
          _tmpClaseOrigen = _cursor.getString(_cursorIndexOfClaseOrigen);
        }
        _item = new LogApp(_tmpFechaHora,_tmpDescripcionError,_tmpClaseOrigen);
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
