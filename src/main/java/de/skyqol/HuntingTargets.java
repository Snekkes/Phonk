package de.skyqol;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Passive nearby-entity hints. Not ESP, x-ray, navigation or world-space highlights. */
public final class HuntingTargets {
    public record Sighting(String type, String name, double distanceMeters, double relativeDegrees) {
        public Sighting(String type, String name, double distanceMeters) {
            this(type, name, distanceMeters, Double.NaN);
        }
    }
    private final List<Sighting> sightings = new ArrayList<>();
    private long lastScan;
    public static String classify(String name, boolean vanillaShulker) {
        if (vanillaShulker) return "shulker_finder";
        if (name == null || name.length() > 256) return null;
        String s = name.replaceAll("§[0-9a-fk-orA-FK-OR]", "").toLowerCase(Locale.ROOT);
        if (s.contains("birries") || s.contains("birrie")) return "birries_highlighter";
        if (s.contains("hideonleaf")) return "hideonleaf_highlighter";
        if (s.contains("shulker")) return "shulker_finder";
        return null;
    }
    public void replace(List<Sighting> current, long now) {
        sightings.clear();
        if (current != null) sightings.addAll(current.stream().limit(64).toList());
        lastScan = now;
    }
    public String display(String type, long now) {
        if (lastScan <= 0 || now < lastScan || now - lastScan > 3000) return null;
        Sighting nearest = null;
        for (Sighting s : sightings) {
            if (!type.equals(s.type()) || !Double.isFinite(s.distanceMeters()) || s.distanceMeters() < 0) continue;
            if (nearest == null || s.distanceMeters() < nearest.distanceMeters()) nearest = s;
        }
        return nearest == null ? null : DirectionHint.nearby(nearest.distanceMeters(),nearest.relativeDegrees());
    }
    public void clear() { sightings.clear(); lastScan = 0; }
}
