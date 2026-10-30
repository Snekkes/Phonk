package de.skyqol;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client-only, passive parsing of selected server messages.
 * This is an initial implementation, not the complete SkyHanni data model.
 * Test patterns against your current Hypixel messages before treating them as accurate.
 */
public final class ForagingTelemetry {
    private static final Pattern SACK = Pattern.compile("(?:^|\\s)([+]\\s*[\\d,]+)\\s+(.{2,60}?)\\s+(?:added to|sent to|in)\\s+(?:your\\s+)?(?:.*?\\s+)?sacks?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern TREE = Pattern.compile("\\btree(?:\\s+(?:break|breaking|progress))?\\s*[:|\\-]?\\s*(\\d{1,3}(?:[.]\\d+)?)\\s*%", Pattern.CASE_INSENSITIVE);
    private static final Pattern SWEEP = Pattern.compile("\\bsweep(?:\\s+(?:progress|charge|cooldown))?\\s*[:|\\-]?\\s*(\\d{1,3}(?:[.]\\d+)?)\\s*%", Pattern.CASE_INSENSITIVE);
    private static final long STALE_MS = 15_000L;
    private final MessageDeduplicator dedupe = new MessageDeduplicator();
    private long logs;
    private long firstLogsAt;
    private long lastLogsAt;
    private double treePercent = -1;
    private long lastTreeAt;
    private double sweepPercent = -1;
    private long lastSweepAt;

    public void accept(String raw, boolean overlay, long nowMillis) {
        accept(raw, overlay, nowMillis, true, true, true);
    }

    /** Never accumulate data for disabled modules. */
    public void accept(String raw, boolean overlay, long nowMillis,
                       boolean trackLogs, boolean trackTree, boolean trackSweep) {
        if (!trackLogs && !trackTree && !trackSweep) return;
        if (raw == null || raw.length() > 2048) return;
        String text = raw.replaceAll("§[0-9a-fk-orA-FK-OR]", "").replaceAll("\\s+", " ").trim();
        if (!overlay && trackLogs) {
            Matcher sack = SACK.matcher(text);
            if (sack.find()) {
                String item = sack.group(2).toLowerCase(Locale.ROOT);
                if (item.contains("log") || item.contains("wood")) {
                    try {
                        long amount = Long.parseLong(sack.group(1).replace("+", "").replace(",", "").replace(" ", ""));
                        if (amount > 0 && amount <= 1_000_000 && Long.MAX_VALUE - logs >= amount && dedupe.accept(text, nowMillis)) { if (firstLogsAt == 0) firstLogsAt = nowMillis; logs += amount; lastLogsAt = nowMillis; }
                    } catch (NumberFormatException ignored) { }
                }
            }
        }
        // Only actionbar data can update per-tree / sweep percentages.
        if (overlay) {
            Matcher tree = TREE.matcher(text);
            if (trackTree && tree.find()) { treePercent = clampPercent(tree.group(1)); lastTreeAt = nowMillis; }
            Matcher sweep = SWEEP.matcher(text);
            if (trackSweep && sweep.find()) { sweepPercent = clampPercent(sweep.group(1)); lastSweepAt = nowMillis; }
        }
    }
    private static double clampPercent(String value) { return Math.max(0, Math.min(100, Double.parseDouble(value))); }
    public long logs() { return logs; }
    public boolean hasLogs() { return lastLogsAt != 0; }
    /** Session observed sacks logs; rate uses elapsed session time, never synthetic profit. */
    public String sessionDisplay(long now) {
        if (!hasLogs()) return null;
        String base = "Logs seen: " + String.format(Locale.ROOT, "%,d", logs);
        long perHour = SessionRate.perHour(logs, firstLogsAt, now);
        return perHour < 0 ? base : base + " | " + String.format(Locale.ROOT, "%,d", perHour) + "/h";
    }
    public String treeDisplay(long now) { return isFresh(now,lastTreeAt) ? "Tree: " + format(treePercent) + "%" : null; }
    public String sweepDisplay(long now) { return isFresh(now,lastSweepAt) ? "Sweep: " + format(sweepPercent) + "%" : null; }
    private static String format(double v) { return String.format(Locale.ROOT, "%.0f", v); }
    private static boolean isFresh(long now, long timestamp) { return timestamp > 0 && now >= timestamp && now - timestamp <= STALE_MS; }
    public void reset() { dedupe.clear(); logs = 0; firstLogsAt = 0; lastLogsAt = 0; treePercent = sweepPercent = -1; lastTreeAt = lastSweepAt = 0; }
}
