#!/bin/sh
set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd -P)"
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
PROPERTIES_FILE=".mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$BASE_DIR/$WRAPPER_JAR" ]; then
  if [ ! -f "$BASE_DIR/$PROPERTIES_FILE" ]; then
    echo "Error: $PROPERTIES_FILE not found." >&2
    exit 1
  fi
  WRAPPER_URL="$(grep -E '^wrapperUrl=' "$BASE_DIR/$PROPERTIES_FILE" | sed 's#wrapperUrl=##')"
  if [ -z "$WRAPPER_URL" ]; then
    echo "Error: wrapperUrl not found in $PROPERTIES_FILE" >&2
    exit 1
  fi
  mkdir -p "$BASE_DIR/.mvn/wrapper"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$BASE_DIR/$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$BASE_DIR/$WRAPPER_JAR"
  else
    echo "Error: Need 'curl' or 'wget' to download Maven Wrapper jar." >&2
    exit 1
  fi
fi

# Pick Java
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="$(command -v java || true)"
fi
if [ -z "$JAVA_EXE" ]; then
  echo "Error: Java not found. Install JDK 17+ or set JAVA_HOME." >&2
  exit 1
fi

exec "$JAVA_EXE" \
  -Dmaven.multiModuleProjectDirectory="$BASE_DIR" \
  -cp "$BASE_DIR/$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"