package com.uninorte.SegundoParcial.work;

import android.content.Context;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkSchedulers {

    private static final String UNIQUE_NAME = "LogSyncLoop";

    public static void enqueueLogSyncLoop(Context ctx) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LogSyncWorker.class)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .addTag(UNIQUE_NAME)
                .build();
        WorkManager.getInstance(ctx).enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.KEEP, req);
    }
}
