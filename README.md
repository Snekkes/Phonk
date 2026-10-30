# SkyQoL 0.9.0 — Minecraft 26.2 / Fabric / Modern Dark

**Development source only — no installable .jar is included.** This project has NOT had a complete Minecraft/Fabric compilation or in-game Hypixel verification. Requires JDK 25, Gradle 9.5.1, Loom 1.17 and Fabric API. The basic Loom version and Fabric API version in the properties file are known published versions for 26.2.

## Scope and known status

18 toggleable client-side modules in `/skyqol`; JSON saved to `.minecraft/config/skyqol.json` with a backup copy at `.minecraft/config/skyqol.json.bak`. If the primary config cannot be parsed, the backup is tried; any broken primary is left intact for recovery. New writes replace both files atomically where supported.
The HUD editor supports drag-to-position, 50–200% per-card scaling, 25–100% opacity, selectable accent colors, group tabs, reset positions and preview. As of v0.9.0 its editor cards use the same dimensions as real HUD cards, and the editor header no longer paints over controls. All toggles default to OFF. Preview is explicitly marked. Module IDs are stable across versions. No automatic gameplay inputs, no server actions, no altered packets.

### Foraging (9/9 modules present; incomplete gameplay features)

- **Foraging Tracker**: counts recognized log/wood items added to sacks (not a complete profit tracker); after 60 seconds it also displays estimated observed logs/hour based on this client session.
- **Tree Progress Display**, **Sweep Overlay**, **Tree Break HUD**: display valid percent values from specific English actionbar patterns, if emitted by the server. Data expires after 15 seconds. Real message formats not verified.
- **Foraging Beacon Solver**: beacon mention detector only. **No puzzle solution yet**.
- **Honeyhive Reminder**: manual test timer, 5 minutes, `/skyqol honeyhive start|clear`. 5 minutes is **not an authenticated game cooldown**.
- **Invisibug**, **Sea Lumies**: named-entity nearby distance hints, **not in-world highlighting**.
- **Critter Capsule Helper**: named-entity nearby hint, **not an actual capsule helper**.

### Hunting (9/9 toggles; 8 have an initial passive UI implementation, one is still a placeholder)

- **Hunting Profit Tracker**: because reliable item prices, acquisition costs and tracked drops are not available, only displays a **manual gross estimate** based on recognized shard quantities; it is **not profit**. Set a user-defined estimated price per shard via `/skyqol shardvalue <coins>`. With no price configured, displays `Value: unknown`. It never claims automatic market pricing.
- **Shard Tracker Display**: counts recognized messages, session only (not persistent); after 60 seconds it also displays a measured shards/hour estimate.
- **Fusion Display**: counts explicit recognized success messages separately from manual F9 notes (not a GUI overlay of recipes).
- **Fusion Keybinds**: client-only F9 manual fusion note and F10 fusion HUD toggle, configurable in Minecraft controls. **These are not actual fusion UI actions**, do not click/confirm fusions, and do not reproduce SkyHanni fusion keybind functionality. Enable this module to use the fusion shortcuts.
- **Lasso HUD**: percentage from experimental English actionbar patterns.
- **Birries**, **Hideonleaf**: nearby named-entity distance hints, **not highlight/waypoints**.
- **Hunting Box Helper**: displays a stored/capacity reading if an actionbar matches a specific pattern (not a functional inventory helper).
- **Shulker Finder**: nearest vanilla Shulker or named shulker distance within 96m, **not pathfinding**.

**The telemetry parsers now respect the individual enabled toggles: turning one OFF prevents its category from collecting new events.** Event counters accumulated while a switch was ON are not automatically erased on toggle, but can be cleared using `/skyqol tracking reset`.

**All game-specific text patterns are synthetic developer examples; none were captured from Hypixel.** Actual game mechanics may use completely different data structures, hidden entities, items or network messages. No feature above should be advertised as fully supported until verified in game. Session counters reset when disconnecting or `/skyqol tracking reset` is entered.

## Build

Install JDK **25** and **Gradle 9.5.1** locally. Inside this project folder run `gradle build` (or build-local.sh / build-local.bat). The compiled jar would then appear in `build/libs` **if compilation succeeds**. Java 25 and Gradle dependencies were not available in the development environment, so the full build is still UNTESTED.

Wrapper metadata is included, but **gradle-wrapper.jar and gradlew scripts are not included**; you need an installed Gradle. The full build might reveal API mapping differences for Minecraft 26.2. Treat compile errors as issues to address, not as a finished release.

## Local testing (no Minecraft libraries needed)

From project root (Java 21+ suffices for the pure-Java tests):

```sh
bash run-tests.sh
```

Note: the Bash brace expansion above works in bash/zsh, not in Windows cmd. All twelve pure-Java tests passed here; use `bash run-tests.sh` to rerun on Linux/macOS. These tests use synthetic example messages; they are not gameplay validation.

## Safety, sources, and licenses

The uploaded third-party JARs were examined for classes/metadata. No original third-party code or JAR files are copied into this project. Treat the user-provided `aaron-mod-...jar` cautiously: its mod metadata does not correspond to its filename and it has not been checked comprehensively. This code does not depend on it.

Source is MIT, subject to the usual dependencies' own licensing. Client-only UI helpers are not a guarantee of compatibility with Hypixel's rules, which can change.

### Next technical blockers

- Full Java 25 / Gradle / Fabric 26.2 compilation (current environment has Java 21 and no resolvable Gradle/Minecraft dependencies).
- Validation on Hypixel using non-sensitive sample messages, inventory structures and entity data.
- Correct Foraging Beacon solver and Fusion interface require actual GUI/puzzle state, not just chat parsing.
- Actual world-space highlights, Hunting Box functionality, reliable shard/price data, real keybind behavior, profile tracking and robust game-mode detection are not yet completed.


## v0.9 improvements

- Unified HUD/editor card dimensions and corrected control layering.
- Added observed sack logs/hour and shards/hour after at least 60 seconds of client-side observation. These are **not** server-certified totals.
- Added `ConfigFileStore` atomic-write backup and fallback for corrupted primary config.
- Fixed per-feature parse gating so disabled subfeatures do not silently gather new counts.
- Moved standalone tests to `src/test/java` and added a 12-test pure-Java harness.
- Added `/skyqol status` with safe, aggregate local counters and Hypixel hostname check.
- The Fabric 26.2 game build is STILL unverified because JDK 25, Gradle 9.5.1 and Maven DNS were not available here. See `TEST-REPORT.md`.

## v0.6 shortcuts and HUD appearance

- `/skyqol` or F8: open Modern Dark settings.
- `/skyqol status`: print enabled-feature count and **aggregate** observed counters for troubleshooting (no raw chat saved or uploaded).
- `/skyqol hud`: open HUD editor directly.
- HUD editor: **Back, Reset, Preview, Foraging/Hunting page, Size -/+, Fade -/+, Color**. Click a card to select; drag to move it. Position, scale, opacity and accent color are persisted per feature in the same JSON. The editor preview does not imply live data availability.
- F9 (while `fusion_keybinds` is enabled): record one *manual note* of a fusion attempt/outcome to help tally personal observations. It is never interpreted as confirmed server data. Key presses within 450 ms are ignored to prevent duplicate notes.
- F10 (while `fusion_keybinds` is enabled): toggle the Fusion Display overlay.
- `/skyqol fusion note|clear`: add/reset manual notes. `/skyqol tracking reset` clears the full session state.
- Change the defaults via Minecraft **Options → Controls → Key Binds → SkyQoL shortcuts**. If F8/F9/F10 collide with existing mods, rebind them there.

Build environment for this update had Java 21 but lacked Gradle 9.5.1, JDK 25 and network resolution to install the dependencies. Attempting `gradle build` returned `gradle: command not found`; no installable JAR was produced.


## Änderungen in v0.7.0 (Sicherheits- und Build-Fixes)

- Die Loom-1.17-Nicht-Remap-Variante nutzt nun `implementation` statt `modImplementation` für Loader und Fabric API (26.2-Anpassung).
- Fortschrittsverarbeitung, Entity-Scan und Live-HUD arbeiten **nur auf Hostnamen `hypixel.net` oder `*.hypixel.net`**. Dies schützt andere Server und Einzelspieler-Welten vor Fehlzählungen; die SkyBlock-Insel selbst wird damit noch nicht erkannt.
- Rasch wiederholte, identische Chat-Nachrichten werden für Ereigniszähler für 250 ms zusammengefasst. Dies reduziert Doppelzählungen, ist aber keine exakte Hypixel-Ereignisidentifikation (auch echte schnelle Wiederholungen könnten zusammenfallen).
- Zwei neue reine Java-Tests für Servererkennung und Duplikatschutz (insgesamt neun Testklassen).
- Die vorhandenen 18 Feature-Schalter und der HUD-Editor bleiben erhalten.

### Kompilierung (nicht mitgeliefert)

Diese ZIP ist ein **Quellcode-Projekt**, keine Minecraft-Mod-JAR. Du brauchst JDK 25 und Gradle 9.5.1, außerdem Online-Zugang zum Herunterladen der Minecraft-/Fabric-Abhängigkeiten.

1. Den Ordner `SkyQoL-Source` entpacken und im Terminal öffnen.
2. `java -version` muss Java 25 oder neuer zeigen und `gradle --version` muss Gradle 9.5.1 zeigen.
3. `gradle build` starten; nach erfolgreichem Build liegt die Mod unter `build/libs/`.
4. `./run-tests.sh` führt nur unabhängige Java-Tests aus (Java 21 reicht für diese Tests), **keinen** Minecraft-Build.

**Bekannte Einschränkungen:** Kein erfolgreicher Fabric-Build oder Hypixel-In-Game-Test in der bereitstehenden Umgebung (kein JDK 25, kein Gradle, keine externen Downloads). Es gibt noch Platzhalter/experimentelle Module – insbesondere Beacon-Solver, World-Highlighter und echte Fusion-Menü-Hilfe. Verwende das Projekt daher noch nicht als fertige Mod. Die JARs der Ursprungsmods werden weder ausgeführt noch in dieses Paket eingebettet.


## v0.8.0 – Richtungs-HUD und praktischer Testmodus

- Foraging- und Hunting-Zielhinweise zeigen für **sichtbare, benannte Entities** jetzt zusätzlich
  `ahead / right / behind / left` in acht Richtungen und die ungefähre Entfernung.
  Kein World-ESP, kein Röntgenblick und keine verifizierte Hypixel-Klassifikation.
- `/skyqol preview on` und `/skyqol preview off` erlauben **klar gekennzeichnete HUD-Vorschau auch im Einzelspieler-Modus**.
  Außerhalb von Hypixel werden weiterhin keine Chat-/Spielereignisse getrackt.
- **All ON / All OFF** im Foraging- oder Hunting-Reiter schaltet die jeweilige Gruppe gemeinsam.
- Nachrichtenerkennung wird nur aufgerufen, wenn mindestens ein relevantes Modul eingeschaltet ist.
- Neue reine Java-Tests für Richtungsberechnung und HUD-Informationen (10 Tests insgesamt).
- `.github/workflows/build.yml` ist eine optionale GitHub-Actions-Buildvorlage mit Java 25 / Gradle 9.5.1.
  Sie garantiert **nicht**, dass das Projekt ohne weitere API-Korrekturen kompiliert.

### Build-Hinweis

Vollständiges `gradle build` konnte in dieser Umgebung nicht ausgeführt werden: nur Java 21,
kein installierter Gradle-Befehl und kein Netzwerkzugang zur Abhängigkeitsauflösung.
Die neue GitHub-Actions-Datei ist lediglich eine **Anleitung für ein künftiges CI**, kein erfolgreicher Build.
**Keine installierbare JAR mitgeliefert.**


## Buildstatus 0.9.1
Siehe [README_BUILD_DE.md](README_BUILD_DE.md) und [BUILD-REPORT.md](BUILD-REPORT.md). Diese Version ist **Quellcode** und eine **Build-Vorbereitung**, keine validierte Minecraft-JAR.
