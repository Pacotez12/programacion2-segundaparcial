package com.uninorte.SegundoParcial.data.repo;

import android.content.Context;

import com.uninorte.SegundoParcial.data.db.AppDatabase;
import com.uninorte.SegundoParcial.data.entity.LogApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class LogRepository {

    private final AppDatabase db;

    public LogRepository(Context ctx) {
        db = AppDatabase.getInstance(ctx);
    }

    // ✅ Fecha actual formateada
    private String now() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
    }

    // ✅ Log no bloqueante (uso normal desde Activities, callbacks, etc.)
    public void add(String origin, String message) {
        Executors.newSingleThreadExecutor().execute(() ->
                db.logDao().insert(new LogApp(
                        now(),
                        message,
                        origin
                ))
        );
    }

    // ✅ Log bloqueante (ideal para WorkManager)
    public void addSync(String origin, String message) {
        db.logDao().insert(new LogApp(
                now(),
                message,
                origin
        ));
    }

    // ✅ Obtener logs (bloqueante)
    public List<LogApp> getAllBlocking() {
        return db.logDao().getAll();
    }

    // ✅ Borrar logs (bloqueante)
    public void clearAllBlocking() {
        db.logDao().clearAll();
    }

    // ✅ Compatibilidad con métodos anteriores
    public void logError(String descripcion, String clase) {
        add(clase, descripcion);
    }
}
