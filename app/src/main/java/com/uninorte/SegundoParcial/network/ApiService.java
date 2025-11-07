package com.uninorte.SegundoParcial.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // ✅ Envío del formulario del cliente
    @Multipart
    @POST(".")
    Call<ResponseBody> uploadClient(
            @Part("datos") RequestBody jsonDatos,
            @Part MultipartBody.Part fotoCasa1,
            @Part MultipartBody.Part fotoCasa2,
            @Part MultipartBody.Part fotoCasa3
    );

    // ✅ Envío de ZIP
    @Multipart
    @POST(".")
    Call<ResponseBody> uploadZip(
            @Part("ci") RequestBody ci,
            @Part MultipartBody.Part archivoZip
    );

    // ✅ Sync logs -- CORREGIDO
    @POST(".")
    Call<ResponseBody> sendSync(@Body RequestBody body);
}
