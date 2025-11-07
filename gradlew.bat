@ECHO OFF
IF EXIST "%~dp0\gradle\wrapper\gradle-wrapper.jar" (
  java -jar "%~dp0\gradle\wrapper\gradle-wrapper.jar" %*
) ELSE (
  ECHO Gradle wrapper jar no encontrado. Use Android Studio para sincronizar.
)
