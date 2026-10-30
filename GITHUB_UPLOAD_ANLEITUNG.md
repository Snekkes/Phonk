# GitHub Actions: Workflow finden (SkyQoL)

**Die ZIP-Datei ist jetzt so aufgebaut, dass alle Dateien direkt im Wurzelverzeichnis liegen.**

So gehst du vor:
1. Entpacke diese ZIP auf deinem Rechner.
2. Öffne GitHub > dein Repository > Code und wähle den Standardbranch (meist `main`).
3. Nutze Add file > Upload files und lade den *Inhalt* des entpackten Verzeichnisses hoch (nicht noch einen Oberordner `SkyQoL` anlegen). Kontrolliere besonders die Datei `.github/workflows/build.yml`.
4. Falls dein System versteckte `.github`-Ordner nicht anzeigt: Auf GitHub im Repository `Add file` > `Create new file`, Dateiname `.github/workflows/build.yml`, und den Inhalt der separat mitgelieferten `SkyQoL-build.yml` einfügen. Änderungen committen.
5. Kontrolliere unter `Code` die Datei im Hauptverzeichnis: `.github/workflows/build.yml` (nicht `SkyQoL/.github/workflows/build.yml`).
6. Wähle `Actions` > `Build SkyQoL (Fabric 26.2)` > `Run workflow`.

Wichtig: Manuelles Starten erfordert, dass die Datei auf dem Standardbranch liegt und Actions im Repository aktiviert ist. Der Build kann danach noch mit Compiler-Fehlern scheitern; dieses Paket behebt nur die Upload-Struktur, nicht etwaige Build-Probleme.
