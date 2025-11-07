# 📱 Segundo Parcial – Programación 2
### Aplicación Android – Entrega Final
**Autor:** Marco Brizuela  
**Usuario GitHub:** Pacotez12

---

## ✅ Nombre de la aplicación
**SegundoParcialApp**

---

## ✅ Descripción general del sistema

La aplicación desarrollada para el Segundo Parcial de Programación 2 permite:

### 🔹 1. Carga de formulario de cliente
El usuario puede completar un formulario con datos personales y adjuntar **3 fotos de la vivienda**.  
El sistema genera un **JSON + fotos** y lo envía mediante una petición `Multipart` a un Webhook.

### 🔹 2. Carga masiva de archivos ZIP
Desde otra pantalla, el usuario puede seleccionar un archivo ZIP y subirlo de forma rápida mediante una petición `Multipart`.

### 🔹 3. Registro de logs en base de datos local (Room + WorkManager)
La app guarda automáticamente logs en una base de datos interna.  
Cada 15 minutos, el sistema envía esos logs al servidor usando un **Worker**, incluso si la app no está abierta.

### 🔹 4. Manejo visual de estados
Ambas pantallas cuentan con:
- Indicador de **carga (loading)**
- Indicador visual de **envío correcto**
- Manejo de errores con Toast + Log interno

### 🔹 5. Arquitectura aplicada
- Room Database para persistencia
- Retrofit + OkHttp para red
- WorkManager para tareas automáticas
- FilePicker para selección de archivos
- Storage Access Framework
- Código modular con repositorios y workers

---

## ✅ Tecnologías utilizadas
- Java
- Android Studio
- Room
- Retrofit / OkHttp
- WorkManager
- JSON
- Multipart Requests

---

## ✅ Estructura general del proyecto

