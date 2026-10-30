package de.skyqol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

/** JDK-only atomic JSON file storage with a last-successful-save backup. */
public final class ConfigFileStore {
    private ConfigFileStore() {}

    public static Path backupPath(Path primary) {
        return primary.resolveSibling(primary.getFileName().toString() + ".bak");
    }

    /** Returns the first parseable primary/backup value, or null if neither is valid. */
    public static <T> T load(Path primary, Function<String, T> parser) {
        for (Path path : new Path[] {primary, backupPath(primary)}) {
            try {
                if (!Files.isRegularFile(path)) continue;
                String content = Files.readString(path, StandardCharsets.UTF_8);
                if (content.length() > 1_048_576) continue; // never read unlimited untrusted config
                T value = parser.apply(content);
                if (value != null) return value;
            } catch (IOException | RuntimeException ignored) {
                // Try the backup. Original files remain untouched for manual recovery.
            }
        }
        return null;
    }

    public static void save(Path primary, String json) throws IOException {
        if (primary.getParent() == null) throw new IOException("Config must have parent directory");
        Files.createDirectories(primary.getParent());
        Path temp = primary.resolveSibling(primary.getFileName() + ".tmp");
        Path backupTemp = primary.resolveSibling(primary.getFileName() + ".bak.tmp");
        Path backup = backupPath(primary);
        try {
            Files.writeString(temp, json, StandardCharsets.UTF_8);
            // Backup is written from the *newly validated serialized value*, not a potentially
            // corrupt old file. If the main file becomes corrupted, the latest save survives.
            Files.copy(temp, backupTemp, StandardCopyOption.REPLACE_EXISTING);
            replace(backupTemp, backup);
            replace(temp, primary);
        } finally {
            Files.deleteIfExists(temp);
            Files.deleteIfExists(backupTemp);
        }
    }

    private static void replace(Path source, Path destination) throws IOException {
        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException unsupported) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
