package de.skyqol;

/** Separate manual notes from detected server events. Never treats a key press as proof of fusion. */
public final class FusionNotes {
    private int manualNotes;
    private long lastNotedAt;

    public boolean record(long now) {
        if (now <= 0 || (lastNotedAt > 0 && now - lastNotedAt < 450)) return false;
        if (manualNotes == Integer.MAX_VALUE) return false;
        manualNotes++;
        lastNotedAt = now;
        return true;
    }

    public int manualNotes() { return manualNotes; }
    public String display() { return manualNotes > 0 ? "Manual fusion notes: " + manualNotes : null; }
    public void reset() { manualNotes = 0; lastNotedAt = 0; }
}
