package de.skyqol;
public final class HudLayoutTest {
    public static void main(String[] args) {
        assert HudLayout.clampScale(.1f) == .5f;
        assert HudLayout.clampScale(5f) == 2f;
        assert HudLayout.clampScale(Float.NaN) == 1f;
        assert HudLayout.clampX(1, 1, 300, 150) == .5f;
        assert Math.abs(HudLayout.clampY(1, 2, 100, 30) - .4f) < .0001f;
        assert HudLayout.clampX(-1, 1, 300, 150) == 0;
        assert HudLayout.clampOpacity(-1f) == .25f;
        assert HudLayout.clampOpacity(2f) == 1f;
        assert HudLayout.clampOpacity(Float.NaN) == .85f;
        assert HudLayout.clampEditorY(0f, 1f, 500, 34, 82) >= .164f;
        assert HudLayout.clampEditorY(1f, 1f, 500, 34, 82) <= .932f;
        System.out.println("HudLayoutTest OK");
    }
}
