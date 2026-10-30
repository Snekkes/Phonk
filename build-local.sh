#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle 9.5.1 missing. Run via GitHub Actions or install Gradle 9.5.1 and JDK 25." >&2
  exit 1
fi
java -version
bash run-tests.sh
gradle --no-daemon clean build
bash verify-built-jar.sh
