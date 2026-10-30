# SkyQoL fuer Minecraft 26.2 bauen

**Wichtig:** Hier liegt Quellcode, keine spielbereite `.jar`. Der Build braucht JDK 25, Gradle 9.5.1 und Internetzugang für Fabric/Minecraft-Abhängigkeiten. Nicht auf Hypixel testen, bevor der Build erfolgreich ist.

## Option A: GitHub Actions (kein lokales Java/Gradle notwendig)

1. Ein GitHub-Repository anlegen.
2. Alle Dateien und Unterordner **aus diesem Paket** ins Hauptverzeichnis hochladen. Auch `.github/workflows/build.yml` muss enthalten sein.
3. Im Repository oben auf **Actions** klicken, dann **Build SkyQoL (Fabric 26.2)** auswählen. Nach einem Push startet der Workflow automatisch, alternativ **Run workflow** nutzen.
4. Erst wenn der Workflow gruen ist: Unter **Artifacts** `SkyQoL-Fabric-26.2-jar` herunterladen.
5. Entpacken: `skyqol-0.9.1+26.2.jar` ist die normale Mod-JAR. Die Sources-JAR gehört nicht in den mods-Ordner.

## Option B: Lokal (mit installiertem JDK 25 und Gradle 9.5.1)

Windows PowerShell oder cmd im Projektordner: `gradle --no-daemon clean build`.

Linux / macOS Terminal: `bash build-local.sh`.

Anschließend liegt die Mod bei `build/libs/skyqol-0.9.1+26.2.jar`, falls die Kompilierung erfolgreich war. `bash verify-built-jar.sh` prüft den Inhalt.

Dieses Paket enthält **keinen Gradle Wrapper JAR** (`gradle-wrapper.jar`). Darum funktioniert `./gradlew` hier nicht; der Workflow installiert Gradle selbst.

Falls der Build fehlschlägt, den Fehlerlog aufbewahren. API-Fehler können erst beim echten Minecraft-Compile erkennbar werden.
