package com.uninorte.SegundoParcial.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.uninorte.SegundoParcial.data.dao.LogDao;
import com.uninorte.SegundoParcial.data.entity.LogApp;

@Database(
        entities = {LogApp.class},
        version = 2,                 // ✅ AUMENTADO PARA REGENERAR LA BD
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LogDao logDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "logs_db"
                            )
                            // ✅ Esto elimina la base de datos vieja cuando cambian entidades
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
