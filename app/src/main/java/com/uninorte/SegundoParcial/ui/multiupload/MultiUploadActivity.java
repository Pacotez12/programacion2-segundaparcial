package com.uninorte.SegundoParcial.ui.multiupload;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uninorte.SegundoParcial.R;
import com.uninorte.SegundoParcial.data.repo.LogRepository;
import com.uninorte.SegundoParcial.databinding.ActivityMultiUploadBinding;
import com.uninorte.SegundoParcial.network.ApiService;
import com.uninorte.SegundoParcial.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiUploadActivity extends AppCompatActivity {

    private ActivityMultiUploadBinding binding;
    private final List<Uri> selectedUris = new ArrayList<>();
    private LogRepository logRepository;

    private Dialog loadingDialog;

    private final ActivityResultLauncher<String[]> pickFiles = registerForActivityResult(
            new ActivityResultContracts.OpenMultipleDocuments(),
            uris -> {
                if (uris != null) {
                    selectedUris.clear();
                    selectedUris.addAll(uris);
                    Toast.makeText(this, "Seleccionados: " + uris.size(), Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        logRepository = new LogRepository(getApplicationContext());

        initLoadingDialog();

        binding.btnPickFiles.setOnClickListener(v -> pickFiles.launch(new String[]{"*/*"}));

        binding.btnUploadZip.setOnClickListener(v -> uploadZip());
    }


    // ===========================================================
    // ✅ LOADING PROFESIONAL
    // ===========================================================
    private void initLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        loadingDialog.setContentView(view);
    }

    private void showLoading(String msg) {
        if (loadingDialog != null) {
            TextView txt = loadingDialog.findViewById(R.id.txtLoading);
            txt.setText(msg);
            loadingDialog.show();
        }
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showSuccessDialog(String msg) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getLayoutInflater().inflate(R.layout.dialog_success, null);
        TextView txt = view.findViewById(R.id.txtSuccess);
        txt.setText(msg);

        dialog.setContentView(view);
        dialog.show();
    }


    // ===========================================================
    // ✅ ZIP
    // ===========================================================
    private byte[] zipUris(List<Uri> uris) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            int i = 1;
            for (Uri uri : uris) {
                zos.putNextEntry(new ZipEntry("file_" + (i++) + ".bin"));
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        zos.write(buf, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }


    // ===========================================================
    // ✅ ENVIAR ZIP
    // ===========================================================
    private void uploadZip() {
        try {
            String ci = binding.etCiMulti.getText().toString();
            if (ci.isEmpty()) {
                Toast.makeText(this, "Ingrese CI", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedUris.isEmpty()) {
                Toast.makeText(this, "Seleccione archivos", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading("Comprimiendo archivos...");

            byte[] zipBytes = zipUris(selectedUris);

            RequestBody zipBody = RequestBody.create(zipBytes, MediaType.parse("application/zip"));
            MultipartBody.Part zipPart =
                    MultipartBody.Part.createFormData("archivoZip", "archivos.zip", zipBody);

            RequestBody ciBody = RequestBody.create(ci, MediaType.parse("text/plain"));

            ApiService api = RetrofitClient.getInstance(this).create(ApiService.class);

            showLoading("Enviando ZIP al servidor...");

            api.uploadZip(ciBody, zipPart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {

                    hideLoading();

                    if (response.isSuccessful()) {
                        showSuccessDialog("ZIP enviado correctamente ✅");
                    } else {
                        Toast.makeText(MultiUploadActivity.this,
                                "Error: código " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call,
                                      @NonNull Throwable t) {

                    hideLoading();

                    logRepository.logError("Fallo ZIP Retrofit: " + t.getMessage(),
                            "MultiUploadActivity");

                    Toast.makeText(MultiUploadActivity.this,
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {

            hideLoading();

            logRepository.logError("Error al comprimir/enviar: " + e.getMessage(),
                    "MultiUploadActivity");

            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
