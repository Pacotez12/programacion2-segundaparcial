package com.uninorte.SegundoParcial.ui.clientform;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.uninorte.SegundoParcial.R;
import com.uninorte.SegundoParcial.data.repo.LogRepository;
import com.uninorte.SegundoParcial.databinding.ActivityClientFormBinding;
import com.uninorte.SegundoParcial.network.ApiService;
import com.uninorte.SegundoParcial.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ProgressBar;


public class ClientFormActivity extends AppCompatActivity {

    private ActivityClientFormBinding binding;
    private Uri uri1, uri2, uri3;
    private Uri tempTarget;
    private LogRepository logRepository;
    private Dialog loadingDialog;
    private ProgressBar progressBar;

    // PERMISO DE CÁMARA
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted ->
                    Toast.makeText(this, granted ? "Permiso cámara concedido" : "Permiso cámara denegado",
                            Toast.LENGTH_SHORT).show()
            );

    // TOMAR FOTO
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logRepository = new LogRepository(getApplicationContext());
        initLoading();

        // Launcher tomar foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (Boolean.TRUE.equals(result) && tempTarget != null) {
                        binding.imgPreview.setImageURI(tempTarget);
                        binding.imgPreview.setVisibility(View.VISIBLE);

                        if (tempTarget == uri1) Toast.makeText(this, "Foto 1 capturada ✅", Toast.LENGTH_SHORT).show();
                        if (tempTarget == uri2) Toast.makeText(this, "Foto 2 capturada ✅", Toast.LENGTH_SHORT).show();
                        if (tempTarget == uri3) Toast.makeText(this, "Foto 3 capturada ✅", Toast.LENGTH_SHORT).show();
                    }
                });

        binding.btnFoto1.setOnClickListener(v -> captureInto(1));
        binding.btnFoto2.setOnClickListener(v -> captureInto(2));
        binding.btnFoto3.setOnClickListener(v -> captureInto(3));
        binding.btnEnviar.setOnClickListener(v -> sendData());

        ensureCameraPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 200);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }
    }

    // ===================== LOADING =====================
    private void initLoading() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        loadingDialog.setContentView(pb);
    }

    private void showLoading() {
        if (!isFinishing() && !loadingDialog.isShowing()) loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    // ===================== PERMISOS =====================
    private void ensureCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    // ===================== RED =====================
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        Network nw = cm.getActiveNetwork();
        if (nw == null) return false;

        NetworkCapabilities cap = cm.getNetworkCapabilities(nw);
        return cap != null && (cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    // ===================== CAPTURAR FOTO =====================
    private Uri createImageUri(String name) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void captureInto(int which) {
        try {
            Uri target = createImageUri("foto_cliente_" + which + "_" + System.currentTimeMillis());

            if (which == 1) uri1 = target;
            if (which == 2) uri2 = target;
            if (which == 3) uri3 = target;

            tempTarget = target;
            takePictureLauncher.launch(target);

        } catch (Exception e) {
            safeLog("Error al capturar foto: " + e.getMessage(), "ClientFormActivity");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ===================== COMPRESIÓN =====================
    private Bitmap decodeBitmapFromUri(Uri uri) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source src = ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(src);
        } else {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            return bmp;
        }
    }

    private Bitmap resize(Bitmap bmp, int maxSize) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        if (w <= maxSize && h <= maxSize) return bmp;

        float ratio = (float) w / h;
        int nw, nh;

        if (ratio > 1) { // horizontal
            nw = maxSize;
            nh = (int) (maxSize / ratio);
        } else {
            nh = maxSize;
            nw = (int) (maxSize * ratio);
        }
        return Bitmap.createScaledBitmap(bmp, nw, nh, true);
    }

    private byte[] compress(Uri uri) {
        try {
            Bitmap bmp = decodeBitmapFromUri(uri);
            Bitmap small = resize(bmp, 1280);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            small.compress(Bitmap.CompressFormat.JPEG, 70, out);
            return out.toByteArray();

        } catch (Exception e) {
            safeLog("compress: " + e.getMessage(), "ClientFormActivity");
            return null;
        }
    }

    private MultipartBody.Part partFromUri(String name, Uri uri) {
        if (uri == null) return null;

        byte[] data = compress(uri);
        if (data == null) return null;

        RequestBody body = RequestBody.create(data, MediaType.parse("image/jpeg"));
        return MultipartBody.Part.createFormData(name, name + ".jpg", body);
    }

    // ===================== ENVIAR FORMULARIO =====================
    private void sendData() {
        if (!isOnline()) {
            showError("Sin conexión a Internet");
            safeLog("Intento sin Internet", "ClientFormActivity");
            return;
        }

        String ci = binding.etCi.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String direccion = binding.etDireccion.getText().toString().trim();
        String telefono = binding.etTelefono.getText().toString().trim();

        if (ci.isEmpty()) { binding.etCi.setError("Campo obligatorio"); return; }
        if (nombre.isEmpty()) { binding.etNombre.setError("Campo obligatorio"); return; }

        ClientPayload payload = new ClientPayload(ci, nombre, direccion, telefono);
        RequestBody json = RequestBody.create(new Gson().toJson(payload),
                MediaType.parse("application/json"));

        MultipartBody.Part p1 = partFromUri("fotoCasa1", uri1);
        MultipartBody.Part p2 = partFromUri("fotoCasa2", uri2);
        MultipartBody.Part p3 = partFromUri("fotoCasa3", uri3);

        ApiService api = RetrofitClient.getInstance(this).create(ApiService.class);

        showLoading();
        api.uploadClient(json, p1, p2, p3).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {

                hideLoading();

                if (response.isSuccessful()) {
                    showSuccess("Datos enviados correctamente ✅");
                    clearForm();
                } else {
                    showError("Código de respuesta: " + response.code());
                    safeLog("Respuesta no exitosa: " + response.code(), "ClientFormActivity");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                hideLoading();
                showError("Error de red: " + t.getMessage());
                safeLog("Retrofit error: " + t.getMessage(), "ClientFormActivity");
            }
        });
    }

    private void clearForm() {
        binding.etCi.setText("");
        binding.etNombre.setText("");
        binding.etDireccion.setText("");
        binding.etTelefono.setText("");

        uri1 = uri2 = uri3 = null;

        binding.imgPreview.setVisibility(View.GONE);
        binding.imgPreview.setImageDrawable(null);
    }

    // ===================== LOG & DIALOGS =====================
    private void safeLog(String msg, String origin) {
        new Thread(() -> {
            try { logRepository.addSync(origin, msg);
            }
            catch (Exception ignored) {}
        }).start();
    }

    private void showError(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSuccess(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Éxito")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    // ===================== PAYLOAD =====================
    static class ClientPayload {
        String ci, nombreCompleto, direccion, telefono;

        ClientPayload(String ci, String nombreCompleto, String direccion, String telefono) {
            this.ci = ci;
            this.nombreCompleto = nombreCompleto;
            this.direccion = direccion;
            this.telefono = telefono;
        }
    }
}
