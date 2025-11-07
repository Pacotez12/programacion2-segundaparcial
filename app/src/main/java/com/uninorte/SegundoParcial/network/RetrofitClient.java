package com.uninorte.SegundoParcial.network;

import android.content.Context;

import com.uninorte.SegundoParcial.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context ctx) {

        if (retrofit == null) {

            // ✅ Logger para ver peticiones/respuestas
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // ✅ Cliente con timeouts extendidos
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)   // antes: default (10s)
                    .readTimeout(30, TimeUnit.SECONDS)      // antes: default (10s)
                    .writeTimeout(30, TimeUnit.SECONDS)     // antes: default (10s)
                    .build();

            // ✅ Retrofit usando tu base_url del strings.xml
            retrofit = new Retrofit.Builder()
                    .baseUrl(ctx.getString(R.string.base_url))
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
