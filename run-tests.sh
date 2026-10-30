#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/tests
base=src/main/java/de/skyqol
unit=src/test/java/de/skyqol
# Deliberately compiles only the Minecraft-independent model classes.
modules=(ForagingTelemetry ForagingAssist ForagingTargets HuntingTelemetry HuntingTargets HudLayout
         FusionNotes HypixelServer MessageDeduplicator DirectionHint SessionRate HudDimensions ConfigFileStore)
sources=()
for module in "${modules[@]}"; do sources+=("$base/$module.java"); done
javac -d build/tests "${sources[@]}" "$unit"/*Test.java
for test in ForagingTelemetryTest ForagingAssistTest ForagingTargetsTest HuntingTelemetryTest \
  HuntingTargetsTest HudLayoutTest FusionNotesTest HypixelServerTest MessageDeduplicatorTest \
  DirectionHintTest SessionRateTest ConfigFileStoreTest; do
  java -ea -cp build/tests "de.skyqol.$test"
done
