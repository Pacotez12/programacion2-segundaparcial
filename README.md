# Aplicacion Examen

Aplicación Android (Java) para **Programación II – Segunda Parcial** que cumple con:

- **Requerimiento 1:** Formulario de cliente (CI, Nombre, Dirección, Teléfono) + captura de **3 fotos** y envío con **Retrofit Multipart** junto a **JSON**.
- **Requerimiento 2:** Selección **múltiple** de archivos, compresión a **.zip** en memoria y envío por **Multipart** con el **CI**.
- **Requerimiento 3:** **Room** con entidad `LogApp` y tabla `logs_app` para auditoría local (fechaHora, descripcionError, claseOrigen). Se registran errores relevantes.
- **Requerimiento 4:** **WorkManager** que se **reprograma cada 5 minutos** (loop de `OneTimeWorkRequest`). Al ejecutarse: obtiene logs, los envía y **borra** si fue exitoso.
- **Requerimiento 5:** Proyecto listo para publicar en GitHub.

> **Endpoint:** por defecto usa `https://webhook.site/` (cambia el valor en `res/values/strings.xml` por tu URL única de webhook).

## Cómo abrir y ejecutar

1. Abre Android Studio ► *Open an existing project* ► selecciona la carpeta `ClienteUploader`.
2. Android Studio usará su Gradle. Si solicita actualizar dependencias, acepta.
3. Conecta un dispositivo o usa un emulador ► *Run*.

## Estructura principal
- `ui/MainActivity` (menú), `ui/clientform/ClientFormActivity` (form + cámara), `ui/multiupload/MultiUploadActivity` (picker + zip).
- `data/entity/LogApp`, `data/dao/LogDao`, `data/db/AppDatabase`, `data/repo/LogRepository`.
- `network/ApiService`, `network/RetrofitClient`.
- `work/LogSyncWorker`, `work/WorkSchedulers`.

## Notas técnicas
- **Cámara:** `ActivityResultContracts.TakePicture` guardando en `MediaStore`.
- **Permisos:** `CAMERA`, lectura de imágenes (según SDK).
- **Zip:** `ZipOutputStream` en memoria para reducir complexidad de I/O.
- **Work cada 5 min:** WorkManager nativamente fija 15 min mínimo en `PeriodicWork`. Para cumplir el requisito de 5 minutos, se implementa un **bucle de `OneTimeWorkRequest`** que se reencola a sí mismo cada 5 minutos.
