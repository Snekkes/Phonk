# SkyQoL Changelog

## 0.9.0 (source-only)

- Correct the HUD editor z-order so buttons are not covered by its header overlay.
- Match the editor preview dimensions to the live HUD dimensions using shared constants.
- Add observed logs/hour and shards/hour summaries, displayed only after 60 seconds of session observation; rates are not verified Hypixel telemetry.
- Prevent toggled-off subfeatures from continuing to collect unrelated events.
- Make config file writes atomic where supported, with a last-good backup and safe fallback on corrupted JSON.
- Move standalone Java tests out of `src/main/java` into `src/test/java` and expand local suite to 12 tests.
- Add `/skyqol status` for aggregate local diagnostics; no raw chat output.
- No full Fabric compilation or Hypixel validation yet. See `TEST-REPORT.md`.

# Changelog

## 0.8.0
- Target detection hints now show relative direction and distance for known visible entities.
- HUD preview can render clearly marked demo cards in singleplayer via /skyqol preview on.
- Group ALL ON/OFF toggles; message parsing skipped when all related toggles off.
- Added optional GitHub Actions build recipe (untested, not a successful Minecraft compilation).
- Added pure-Java direction tests.

# Changelog

## v0.7.0 – Fabric Build & Tracking Harden

- Use `implementation` dependencies with non-remapping Fabric Loom for Minecraft 26.2.
- Gate chat telemetry, target scans and live HUD to exact Hypixel hostnames.
- Prevent rapid duplicate count events for sack logs, shards and fusion messages (250ms).
- Add pure Java test suites for hostname gate and duplicate event filter.
- Remains source-only and not verified as a Fabric build or live Hypixel mod.

# SkyQoL Changelog

## 0.6.0 (development source, no installable JAR)

- Adds three configurable client-only shortcuts under Options > Controls > Key Binds (F8, F9, F10).
  - F8 opens SkyQoL settings.
  - F9 adds an explicit *manual fusion note* (never performs a fusion or claims success), gated behind the `fusion_keybinds` feature switch.
  - F10 toggles the `fusion_display` module, also gated behind `fusion_keybinds`.
- Adds HUD editor page switch between Foraging and Hunting to reduce overlap.
- Adds individually stored HUD background opacity (25%-100%) and 5 accent-color presets.
- Respects opacity/accent settings in HUD display, and reserves editor header when dragging cards.
- Adds `FusionNotes` isolated pure Java unit test; 7/7 pure Java test classes pass locally.
- Adds `/skyqol hud`, `/skyqol fusion note` and `/skyqol fusion clear` commands.
- All 18 feature toggles remain, with stable config IDs.

### Known gaps

- **This is not yet a working, tested Hypixel mod.** The full Fabric 26.2 build and in-game verification have not happened.
- Fusion keybindings are *not* recipe or inventory automation, and do not implement SkyHanni's behavior.
- Foraging Beacon Solver is *not* implemented, and most tracker/highlighter modules still use unverified, experimental game-message/name matching.
- World-space highlights and actual Hunting Box/Critter Capsule assistance are not yet implemented.
- This project was not linked, merged or copied from any third-party source code; references were used only for feature identification.
- Prior third-party JARs are not distributed in this archive.


## 0.9.1 — Buildvorbereitung
- Loom-Abhängigkeit auf veröffentlichtes `1.17.21` gepinnt.
- GitHub Actions für echte Minecraft/Fabric-Kompilierung und Artefaktprüfung aktualisiert.
- Installierbare JAR wird nur nach erfolgreichem Build und Verifikation bereitgestellt.
- Noch kein lokaler Fabric-Build aufgrund fehlender JDK-25-/Gradle-Werkzeuge.
