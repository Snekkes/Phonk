package de.skyqol;

import java.nio.file.Files;
import java.nio.file.Path;

/** Pure-Java exercise of config writes, backup fallback and broken-data preservation. */
public final class ConfigFileStoreTest {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("skyqol-config-test-");
        Path file = dir.resolve("skyqol.json");
        try {
            ConfigFileStore.save(file, "{ONE}");
            assert "{ONE}".equals(Files.readString(file));
            assert "{ONE}".equals(Files.readString(ConfigFileStore.backupPath(file)));
            ConfigFileStore.save(file, "{TWO}");
            assert "{TWO}".equals(ConfigFileStore.load(file, s -> s.startsWith("{") ? s : null));
            Files.writeString(file, "BROKEN");
            assert "{TWO}".equals(ConfigFileStore.load(file, s -> s.startsWith("{") ? s : null));
            assert "BROKEN".equals(Files.readString(file)); // no silent destruction
            Files.writeString(ConfigFileStore.backupPath(file), "ALSO BROKEN");
            assert ConfigFileStore.load(file, s -> s.startsWith("{") ? s : null) == null;
            System.out.println("ConfigFileStoreTest OK");
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(ConfigFileStore.backupPath(file));
            Files.deleteIfExists(dir);
        }
    }
}
