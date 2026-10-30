#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
JAR="build/libs/skyqol-0.9.1+26.2.jar"
if [ ! -s "$JAR" ]; then
  echo "ERROR: normal mod JAR missing: $JAR" >&2
  exit 1
fi
for item in 'fabric.mod.json' 'de/skyqol/SkyQolClient.class' 'de/skyqol/ConfigScreen.class' 'de/skyqol/HudEditorScreen.class'; do
  if ! jar tf "$JAR" | grep -Fxq "$item"; then
    echo "ERROR: missing entry in $JAR: $item" >&2
    exit 1
  fi
done
python3 - "$JAR" <<'PYTHON'
import json, sys, zipfile
jar = sys.argv[1]
with zipfile.ZipFile(jar) as z:
    meta = json.loads(z.read('fabric.mod.json'))
    assert meta['id'] == 'skyqol', meta
    assert meta['version'] == '0.9.1+26.2', meta
    client = z.read('de/skyqol/SkyQolClient.class')
    major = int.from_bytes(client[6:8], 'big')
    assert major == 69, f'Expected Java 25 class file version 69, got {major}'
print('Fabric metadata and Java 25 bytecode verified.')
PYTHON
sha256sum "$JAR"
echo "Build output is ready: $JAR"
