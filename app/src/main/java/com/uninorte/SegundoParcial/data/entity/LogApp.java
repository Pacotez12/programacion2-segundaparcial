package com.uninorte.SegundoParcial.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logs_app")
public class LogApp {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String fechaHora;
    public String descripcionError;
    public String claseOrigen;

    public LogApp(String fechaHora, String descripcionError, String claseOrigen) {
        this.fechaHora = fechaHora;
        this.descripcionError = descripcionError;
        this.claseOrigen = claseOrigen;
    }
}
