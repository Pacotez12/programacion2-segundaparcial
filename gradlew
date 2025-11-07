#!/usr/bin/env sh
DIR="$( cd "$( dirname "$0" )" >/dev/null 2>&1 && pwd )"
if [ -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
else
  echo "Gradle wrapper jar no encontrado. Use Android Studio para sincronizar."
fi
