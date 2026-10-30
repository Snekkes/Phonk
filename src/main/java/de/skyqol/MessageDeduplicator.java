package de.skyqol;

import java.util.LinkedHashMap;
import java.util.Map;

/** Short-lived duplicate suppression for counter events; status messages are unaffected. */
public final class MessageDeduplicator {
    private static final int MAX = 64;
    private static final long WINDOW_MS = 250;
    private final LinkedHashMap<String,Long> last = new LinkedHashMap<>();

    /** Returns true for a new event; false for a repeated message within 250 ms. */
    public boolean accept(String key, long now) {
        if (key == null || key.length() > 2048 || now <= 0) return false;
        Long previous = last.get(key);
        if (previous != null && now >= previous && now - previous < WINDOW_MS) return false;
        last.put(key,now);
        if (last.size() > MAX) {
            String oldest = last.keySet().iterator().next();
            last.remove(oldest);
        }
        return true;
    }

    public void clear() { last.clear(); }
    public int rememberedKeys() { return last.size(); }
}
