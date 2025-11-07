package com.uninorte.SegundoParcial.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.uninorte.SegundoParcial.data.repo.LogRepository;
import com.uninorte.SegundoParcial.databinding.ActivityMainBinding;
import com.uninorte.SegundoParcial.ui.clientform.ClientFormActivity;
import com.uninorte.SegundoParcial.ui.multiupload.MultiUploadActivity;
import com.uninorte.SegundoParcial.work.WorkSchedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnClientForm.setOnClickListener(v ->
                startActivity(new Intent(this, ClientFormActivity.class)));

        binding.btnMultiUpload.setOnClickListener(v ->
                startActivity(new Intent(this, MultiUploadActivity.class)));

        // ✅ Generar un log de prueba al entrar a la app
        LogRepository repo = new LogRepository(this);
        repo.add("MainActivity", "Log de prueba generado automáticamente al abrir la app ✅");

        // ✅ Iniciar el ciclo de sincronización cada 5 minutos
        WorkSchedulers.enqueueLogSyncLoop(this);
    }
}
