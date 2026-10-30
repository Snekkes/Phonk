# SkyQoL v0.9.0 — Test report (2026-10-30)

## Verified in this workspace

- Starting point: previously delivered v0.8.0 source ZIP, unpacked in the active workspace.
- `bash run-tests.sh`: **12 of 12 standalone Java test classes passed** with OpenJDK 21, including newly added session-rate and config-backup tests.
- All pure Java source used by the test harness compiled with `javac` 21.
- `gradle.properties` targets Minecraft 26.2, Fabric Loader 0.19.3, Fabric API 0.157.0+26.2, Loom 1.17, Java 25 and Gradle 9.5.1.
- ZIP integrity verified separately at packaging time.

## Not tested / not claimed

- **No Minecraft or Fabric binary has been compiled successfully in this workspace.** The container has Java 21 and no installed Gradle, and outbound Maven DNS lookup fails. The project requires JDK 25 and dependencies that could not be fetched.
- No in-game launch, FPS benchmark, Hypixel connection or real server message tests were possible.
- The real beacon puzzle solver, world-space highlighters, Critter Capsule Helper mechanics, real Fusion UI integration, accurate Hunting profit and other features remain incomplete or experimental.
- The project is source-only. No installable `.jar` is present.

## How to verify a real build

1. Install JDK 25 and Gradle 9.5.1, or place the project in a GitHub repository and trigger `.github/workflows/build.yml`.
2. Run `bash run-tests.sh` and then `gradle --no-daemon build`.
3. Investigate any compiler/API errors instead of assuming the build succeeded.
4. If the Gradle build succeeds, test the resulting JAR in a **separate** Fabric 26.2 profile and verify behavior with benign live observations, not automation.
5. Keep the 18 experimental toggles OFF until each feature has been validated individually.

## Scope and data handling

SkyQoL only processes local game UI events in memory and saves the user's local config JSON. The new `/skyqol status` outputs aggregate session counters. The project does not deliberately save raw messages, transmit analytics or automate inputs.
