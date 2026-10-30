@echo off
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle 9.5.1 fehlt. Alternativ GitHub Actions verwenden.
  exit /b 1
)
java -version
call gradle --no-daemon clean build
if errorlevel 1 exit /b 1
if not exist "build\libs\skyqol-0.9.1+26.2.jar" (
  echo Die erwartete installierbare Mod-JAR fehlt.
  exit /b 1
)
jar tf "build\libs\skyqol-0.9.1+26.2.jar" > nul
if errorlevel 1 exit /b 1
echo Build erfolgreich: build\libs\skyqol-0.9.1+26.2.jar
