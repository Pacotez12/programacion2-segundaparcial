package com.uninorte.SegundoParcial;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;

import com.uninorte.SegundoParcial.work.LogSyncWorker;

import java.util.concurrent.TimeUnit;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ Requiere Internet para sincronizar
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // ✅ Cada 5 minutos (Android puede forzar 15)
        PeriodicWorkRequest syncRequest =
                new PeriodicWorkRequest.Builder(LogSyncWorker.class, 5, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag("LogSync")
                        .build();

        // ✅ Solo un worker activo siempre
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "LogSyncWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }
}
