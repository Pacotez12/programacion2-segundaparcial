package com.uninorte.SegundoParcial.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.uninorte.SegundoParcial.data.entity.LogApp;
import com.uninorte.SegundoParcial.data.repo.LogRepository;
import com.uninorte.SegundoParcial.network.ApiService;
import com.uninorte.SegundoParcial.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LogSyncWorker extends Worker {

    private final LogRepository logRepository;

    public LogSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        logRepository = new LogRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            // ✅ 1. Obtener logs
            List<LogApp> logs = logRepository.getAllBlocking();

            if (logs.isEmpty()) {
                return Result.success();
            }

            // ✅ 2. Envolver en objeto JSON
            Map<String, Object> payload = new HashMap<>();
            payload.put("logs", logs);

            String json = new Gson().toJson(payload);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            // ✅ 3. Llamar al backend
            ApiService api = RetrofitClient.getInstance(getApplicationContext()).create(ApiService.class);

            Call<ResponseBody> call = api.sendSync(body);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful()) {
                logRepository.clearAllBlocking();
                return Result.success();
            } else {
                return Result.retry();
            }

        } catch (Exception e) {
            return Result.retry();
        }
    }
}
