package de.skyqol;

/** Safe observation-based rate; requires a full minute to avoid misleading spikes. */
public final class SessionRate {
    private static final long MIN_ELAPSED_MS = 60_000L;
    private static final long MILLIS_PER_HOUR = 3_600_000L;
    private SessionRate() {}

    /** -1 means insufficient session data. Does not guess an observed game rate. */
    public static long perHour(long observed, long firstAt, long now) {
        if (observed < 0 || firstAt <= 0 || now < firstAt || now - firstAt < MIN_ELAPSED_MS) return -1;
        double rate = (double) observed * MILLIS_PER_HOUR / (now - firstAt);
        if (!Double.isFinite(rate) || rate < 0) return -1;
        return rate >= Long.MAX_VALUE ? Long.MAX_VALUE : Math.round(rate);
    }
}
