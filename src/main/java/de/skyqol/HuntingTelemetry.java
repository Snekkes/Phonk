package de.skyqol;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Passive, best-effort English text parser for Hunting-related game messages.
 * It counts only explicit observations; NEVER estimates prices or controls gameplay.
 * Patterns are developer fixtures, not verified Hypixel 26.2 messages.
 */
public final class HuntingTelemetry {
    private static final Pattern FORMAT = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
    private static final Pattern SHARD_GAIN = Pattern.compile(
        "(?:^|\\b)(?:you\\s+(?:received|obtained|found|gained)|\\+)\\s*[:+]?\\s*(\\d{1,6})\\s+(?:[a-z][a-z '-]{0,48}\\s+)?(?:attribute\\s+)?shards?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHARD_SUFFIX = Pattern.compile(
        "\\b(?:attribute\\s+)?shards?\\s*(?:gained|obtained|received)\\s*[:+]?\\s*(\\d{1,6})\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FUSION = Pattern.compile("\\b(?:fusion successful|fusion complete|successfully fused)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LASSO = Pattern.compile("\\blasso\\s*(?:progress|strength|charge)?\\s*[:|\\-]?\\s*(\\d{1,3}(?:\\.\\d+)?)\\s*%", Pattern.CASE_INSENSITIVE);
    private static final Pattern HUNTING_BOX = Pattern.compile("\\bhunting\\s*box\\s*(?:progress|stored|capacity)?\\s*[:|\\-]?\\s*(\\d{1,6})\\s*/\\s*(\\d{1,6})\\b", Pattern.CASE_INSENSITIVE);
    private static final long ACTIVITY_TTL = 15_000;
    private final MessageDeduplicator shardDedupe = new MessageDeduplicator();
    private final MessageDeduplicator fusionDedupe = new MessageDeduplicator();
    private long shards;
    private long firstShard;
    private long lastShard;
    private int fusions;
    private long lastFusion;
    private double lassoPercent = -1;
    private long lastLasso;
    private int boxStored = -1;
    private int boxCapacity = -1;
    private long lastBox;

    public void accept(String raw, boolean overlay, long now) {
        accept(raw, overlay, now, true, true, true, true);
    }

    /** A feature that is OFF must not update its counters in the background. */
    public void accept(String raw, boolean overlay, long now,
                       boolean trackShards, boolean trackFusion, boolean trackLasso, boolean trackBox) {
        if (!trackShards && !trackFusion && !trackLasso && !trackBox) return;
        if (raw == null || raw.length() > 2048 || now <= 0) return;
        String msg = FORMAT.matcher(raw).replaceAll("").replaceAll("\\s+", " ").trim();
        if (msg.isEmpty()) return;
        if (!overlay) {
            Matcher m = SHARD_GAIN.matcher(msg);
            boolean found = m.find();
            if (!found) {
                m = SHARD_SUFFIX.matcher(msg);
                found = m.find();
            }
            if (trackShards && found) {
                long count = parseLong(m.group(1));
                if (count > 0 && count <= 100_000 && shards <= Long.MAX_VALUE - count && shardDedupe.accept(msg, now)) {
                    if (firstShard == 0) firstShard = now;
                    shards += count;
                    lastShard = now;
                }
            }
            if (trackFusion && FUSION.matcher(msg).find() && fusionDedupe.accept(msg,now)) {
                if (fusions < Integer.MAX_VALUE) fusions++;
                lastFusion = now;
            }
        } else {
            Matcher lasso = LASSO.matcher(msg);
            if (trackLasso && lasso.find()) {
                double value = Double.parseDouble(lasso.group(1));
                if (value >= 0 && value <= 100) { lassoPercent = value; lastLasso = now; }
            }
            Matcher box = HUNTING_BOX.matcher(msg);
            if (trackBox && box.find()) {
                long stored = parseLong(box.group(1)), capacity = parseLong(box.group(2));
                if (capacity > 0 && capacity <= 1_000_000 && stored >= 0 && stored <= capacity) {
                    boxStored = (int) stored;
                    boxCapacity = (int) capacity;
                    lastBox = now;
                }
            }
        }
    }

    private static long parseLong(String value) {
        try { return Long.parseLong(value); } catch (NumberFormatException e) { return -1; }
    }
    private static boolean fresh(long now, long timestamp) { return timestamp > 0 && now >= timestamp && now - timestamp <= ACTIVITY_TTL; }
    public long shards() { return shards; }
    public int fusions() { return fusions; }
    public String shardDisplay() { return lastShard > 0 ? "Shards seen: " + shards : null; }
    public String shardSessionDisplay(long now) {
        String base = shardDisplay();
        if (base == null) return null;
        long perHour = SessionRate.perHour(shards, firstShard, now);
        return perHour < 0 ? base : base + " | " + String.format(Locale.ROOT, "%,d", perHour) + "/h";
    }
    public String fusionDisplay(long now) {
        return lastFusion == 0 ? null : "Fusions seen: " + fusions + (fresh(now,lastFusion) ? " (recent)" : "");
    }
    public String lassoDisplay(long now) {
        return fresh(now,lastLasso) ? String.format(Locale.ROOT, "Lasso: %.0f%%", lassoPercent) : null;
    }
    public String huntingBoxDisplay(long now) {
        return fresh(now,lastBox) ? "Hunting Box: " + boxStored + "/" + boxCapacity : null;
    }
    /** This is NOT net profit. Acquisition costs and actual prices are unknown. */
    public String huntingValueDisplay(long pricePerShard) {
        if (lastShard == 0) return null;
        if (pricePerShard <= 0) return "Value: unknown (set /skyqol shardvalue)";
        if (shards > Long.MAX_VALUE / pricePerShard) return "Estimated gross value: overflow";
        return "Gross estimate: " + String.format(Locale.ROOT,"%,d", shards * pricePerShard) + " coins";
    }
    public void reset() {
        shardDedupe.clear(); fusionDedupe.clear();
        shards = 0; firstShard = 0; lastShard = 0; fusions = 0; lastFusion = 0;
        lassoPercent = -1; lastLasso = 0;
        boxStored = -1; boxCapacity = -1; lastBox = 0;
    }
}
