package de.skyqol;

import java.util.Locale;

/** Client-side relative heading for visible, named entities only. No x-ray or automation. */
public final class DirectionHint {
    private DirectionHint() {}

    /**
     * Vanilla yaw 0 points south (+Z); positive yaw rotates towards west (-X).
     * xOffset/zOffset are target minus player in world blocks.
     * Returns [-180, 180), where 0 means straight ahead.
     */
    public static double relativeDegrees(double xOffset, double zOffset, double playerYaw) {
        if (!Double.isFinite(xOffset) || !Double.isFinite(zOffset) || !Double.isFinite(playerYaw)
            || (xOffset == 0 && zOffset == 0)) return Double.NaN;
        double targetYaw = Math.toDegrees(Math.atan2(-xOffset, zOffset));
        double a = (targetYaw - playerYaw) % 360d;
        if (a < -180d) a += 360d;
        if (a >= 180d) a -= 360d;
        return a;
    }

    /** Minecraft text-friendly, eight-direction indicator, avoiding font-dependent arrows. */
    public static String label(double degrees) {
        if (!Double.isFinite(degrees)) return "unknown";
        double angle = (degrees % 360d + 360d) % 360d;
        int idx = ((int) Math.floor((angle + 22.5d) / 45d)) % 8;
        return switch(idx) {
            case 0 -> "ahead";
            case 1 -> "front-left";
            case 2 -> "left";
            case 3 -> "back-left";
            case 4 -> "behind";
            case 5 -> "back-right";
            case 6 -> "right";
            default -> "front-right";
        };
    }

    /** Direction is a hint only; classification and distances may be wrong on Hypixel. */
    public static String nearby(double distanceMeters, double relativeDegrees) {
        if (!Double.isFinite(distanceMeters) || distanceMeters < 0) return "Nearby: unknown";
        if (!Double.isFinite(relativeDegrees)) return String.format(Locale.ROOT,"Nearby: %.0f m",distanceMeters);
        return String.format(Locale.ROOT,"%s / %.0f m",label(relativeDegrees),distanceMeters);
    }
}
