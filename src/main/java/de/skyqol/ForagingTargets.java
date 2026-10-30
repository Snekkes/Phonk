package de.skyqol;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Passive name matching, not a world-space highlighter or a capsule solver. */
public final class ForagingTargets {
    public record Sighting(String type, String displayName, double distanceMeters, double relativeDegrees) {
        /** Compatibility for old tests / sightings without position data. */
        public Sighting(String type, String displayName, double distanceMeters) {
            this(type, displayName, distanceMeters, Double.NaN);
        }
    }
    private final List<Sighting> sightings = new ArrayList<>();
    private long updatedAt;

    public static String classify(String name) {
        if (name == null || name.length() > 256) return null;
        String cleaned = name.replaceAll("§[0-9a-fk-orA-FK-OR]", "").trim().toLowerCase(Locale.ROOT);
        if (cleaned.contains("invisibug")) return "invisibug_highlighter";
        if (cleaned.contains("sea lumies") || cleaned.contains("sea lumie")) return "sea_lumies_highlighter";
        if (cleaned.contains("critter capsule")) return "critter_capsule";
        return null;
    }
    public void replace(List<Sighting> current, long now) {
        sightings.clear();
        if (current != null) sightings.addAll(current.stream().limit(64).toList());
        updatedAt = now;
    }
    public String display(String type, long now) {
        if (updatedAt == 0 || now - updatedAt > 3000 || now < updatedAt) return null;
        Sighting nearest = null;
        for (Sighting s : sightings) {
            if (!type.equals(s.type()) || !Double.isFinite(s.distanceMeters()) || s.distanceMeters() < 0) continue;
            if (nearest == null || s.distanceMeters() < nearest.distanceMeters()) nearest = s;
        }
        return nearest == null ? null : DirectionHint.nearby(nearest.distanceMeters(),nearest.relativeDegrees());
    }
    public void clear() { sightings.clear(); updatedAt = 0; }
}
