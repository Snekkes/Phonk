package de.skyqol;

/** UI-independent clamping/math shared by real HUD renderer and drag editor. */
public final class HudLayout {
    private HudLayout() {}
    public static float clampScale(float value) {
        if (!Float.isFinite(value)) return 1f;
        return Math.max(.5f,Math.min(2f,value));
    }
    public static float clampOpacity(float value) {
        if (!Float.isFinite(value)) return .85f;
        return Math.max(.25f, Math.min(1f, value));
    }
    /** Editor controls occupy a fixed-height header and must not overlap draggable cards. */
    public static float clampEditorY(float y, float scale, int height, int cardHeight, int reservedTop) {
        float min = Math.max(0, reservedTop) / (float)Math.max(1, height);
        float max = Math.max(min, 1f - cardHeight * clampScale(scale) / Math.max(1, height));
        return Math.max(min, Math.min(max, Float.isFinite(y) ? y : min));
    }
    public static float clampX(float x, float scale, int width, int cardWidth) {
        return clampNorm(x, 1f - (cardWidth * clampScale(scale)) / Math.max(1, width));
    }
    public static float clampY(float y, float scale, int height, int cardHeight) {
        return clampNorm(y, 1f - (cardHeight * clampScale(scale)) / Math.max(1, height));
    }
    private static float clampNorm(float p, float max) {
        if (!Float.isFinite(p)) return 0f;
        return Math.max(0f, Math.min(Math.max(0f,max),p));
    }
}
