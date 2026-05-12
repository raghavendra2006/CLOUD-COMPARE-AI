#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper startup script for Unix/Linux/macOS.
# Generated/added to support `./mvnw test` on non-Windows systems.
# ----------------------------------------------------------------------------

BASE_DIR=$(dirname "$0")
BASE_DIR=$(cd "$BASE_DIR" && pwd)

# Maven Wrapper properties
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Missing: $WRAPPER_PROPERTIES" >&2
  exit 1
fi

# Extract distribution URL and wrapper jar location
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Missing wrapper jar: $WRAPPER_JAR" >&2
  exit 1
fi

# Java executable
JAVA_HOME_DIR="${JAVA_HOME:-}"
if [ -n "$JAVA_HOME_DIR" ] && [ -x "$JAVA_HOME_DIR/bin/java" ]; then
  JAVA_CMD="$JAVA_HOME_DIR/bin/java"
else
  JAVA_CMD="java"
fi

# JVM args
MAVEN_OPTS="${MAVEN_OPTS:-}"

# If MAVEN_PROJECTBASEDIR is not set, set it
if [ -z "$MAVEN_PROJECTBASEDIR" ]; then
  MAVEN_PROJECTBASEDIR="$BASE_DIR"
fi

exec "$JAVA_CMD" ${MAVEN_OPTS} -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain \
  -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  "$@"
