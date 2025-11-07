package com.uninorte.SegundoParcial.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.uninorte.SegundoParcial.data.entity.LogApp;

import java.util.List;

@Dao
public interface LogDao {
    @Insert
    long insert(LogApp log);

    @Query("SELECT * FROM logs_app ORDER BY id ASC")
    List<LogApp> getAll();

    @Query("DELETE FROM logs_app")
    void clearAll();
}
