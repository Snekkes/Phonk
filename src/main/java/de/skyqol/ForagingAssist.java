package de.skyqol;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Passive and deliberately conservative UI helpers. No automated interaction, clicking,
 * gameplay inputs or server packets. Patterns are experimental until verified in-game.
 */
public final class ForagingAssist {
    private static final Pattern TREE_BREAK = Pattern.compile("\\b(?:tree\\s*break(?:ing)?|breaking\\s*tree)\\s*(?:progress)?\\s*[:|\\-]?\\s*(\\d{1,3}(?:[.]\\d+)?)\\s*%", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEACON = Pattern.compile("\\b(?:foraging\\s+)?beacon\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern HONEYHIVE = Pattern.compile("\\bhoneyhive\\b", Pattern.CASE_INSENSITIVE);
    private static final long DATA_TTL_MS = 15_000;
    private static final long BEACON_TTL_MS = 30_000;
    private static final long HONEYHIVE_REMINDER_MS = 5 * 60_000;
    private double treeBreakPercent = -1;
    private long lastTreeBreak;
    private long lastBeacon;
    private long honeyhiveStartedAt;
    private long lastHoneyhiveMention;

    public void accept(String raw, boolean overlay, long now) {
        accept(raw, overlay, now, true, true, true);
    }

    public void accept(String raw, boolean overlay, long now,
                       boolean trackTreeBreak, boolean trackBeacon, boolean trackHoneyhive) {
        if (!trackTreeBreak && !trackBeacon && !trackHoneyhive) return;
        if (raw == null || raw.length() > 2048 || now < 0) return;
        String msg = raw.replaceAll("§[0-9a-fk-orA-FK-OR]", "").replaceAll("\\s+", " ").trim();
        if (overlay && trackTreeBreak) {
            Matcher tree = TREE_BREAK.matcher(msg);
            if (tree.find()) {
                double value = Double.parseDouble(tree.group(1));
                if (value >= 0 && value <= 100) {
                    treeBreakPercent = value;
                    lastTreeBreak = now;
                }
            }
        }
        if (trackBeacon && BEACON.matcher(msg).find()) lastBeacon = now;
        if (trackHoneyhive && HONEYHIVE.matcher(msg).find()) lastHoneyhiveMention = now;
    }

    public String treeBreakDisplay(long now) {
        return fresh(now, lastTreeBreak, DATA_TTL_MS)
            ? "Break: " + String.format(Locale.ROOT,"%.0f%%",treeBreakPercent) : null;
    }

    /** A detected beacon is NOT a puzzle solution; no solver is claimed. */
    public String beaconDisplay(long now) {
        return fresh(now,lastBeacon,BEACON_TTL_MS) ? "Beacon detected (solver pending)" : null;
    }

    /** Manual opt-in timer: /skyqol honeyhive start. */
    public void startHoneyhive(long now) { honeyhiveStartedAt = Math.max(1,now); }
    public void clearHoneyhive() { honeyhiveStartedAt = 0; }
    public String honeyhiveDisplay(long now) {
        if (honeyhiveStartedAt == 0 || now < honeyhiveStartedAt) return null;
        long left = Math.max(0, HONEYHIVE_REMINDER_MS - (now - honeyhiveStartedAt));
        if (left == 0) return "Honeyhive: reminder due";
        long seconds = (left + 999) / 1000;
        return String.format(Locale.ROOT,"Honeyhive: %d:%02d", seconds / 60, seconds % 60);
    }
    public boolean hasRecentHoneyhiveMention(long now) { return fresh(now,lastHoneyhiveMention,DATA_TTL_MS); }
    private static boolean fresh(long now,long at,long ttl) { return at>0 && now>=at && now-at<=ttl; }
    public void reset() {
        treeBreakPercent=-1; lastTreeBreak=0; lastBeacon=0;
        honeyhiveStartedAt=0; lastHoneyhiveMention=0;
    }
}
