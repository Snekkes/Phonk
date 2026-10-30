# SkyQoL 0.9.1: build validation report

Date: 2026-10-30

## What was executed in this workspace

- `java -version`: OpenJDK 21.0.12.1.
- `gradle --no-daemon build`: FAILED immediately, because `gradle` is not installed (`command not found`, exit 127).
- Network test: DNS resolution of services.gradle.org failed. Therefore JDK 25/Gradle/Fabric dependencies could not be downloaded.
- `bash run-tests.sh`: 12 independent Java unit test classes PASS (model and parser code only).

## What changed to prepare a genuine build

- Pinned Fabric Loom to **1.17.21**, verified published in Fabric Maven (the old plain `1.17` coordinate is unverified).
- Kept Gradle **9.5.1**, Fabric Loader **0.19.3**, Fabric API **0.157.0+26.2**, Minecraft **26.2**, Java **25**.
- Added a GitHub Actions workflow for JDK 25 + Gradle 9.5.1, unit tests, full `clean build` and artifact validation.
- Added `verify-built-jar.sh`: validates the mod metadata, essential compiled entrypoints, and Java 25 bytecode.

## What has NOT been verified

- No full Fabric compile or Minecraft client launch was possible here.
- Do not treat the source zip as an installable mod.
- The CI workflow may reveal source-level API/compile errors that cannot be checked without real dependencies; these errors must be fixed before a usable release.
- The 18 module toggles are not proof of complete Hypixel feature implementations.

## How to get the real mod jar

Upload the **contents** of this package to the root of a GitHub repository and run **Actions > Build SkyQoL (Fabric 26.2) > Run workflow**, or push a commit to trigger the workflow automatically.

If the workflow succeeds, download `SkyQoL-Fabric-26.2-jar` from the run's Artifacts section and extract `skyqol-0.9.1+26.2.jar` into the Fabric mods folder. If it fails, keep the build log: do not install an unverified jar.
