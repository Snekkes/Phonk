package de.skyqol;

public final class DirectionHintTest {
    private static void near(double actual, double expected) {
        assert Math.abs(actual - expected) < 0.001 : actual + " != " + expected;
    }
    public static void main(String[] args) {
        near(DirectionHint.relativeDegrees(0, 5, 0),0); // south-facing
        near(DirectionHint.relativeDegrees(-5, 0, 0),90); // target west -> left
        near(DirectionHint.relativeDegrees(5, 0, 0),-90); // target east -> right
        near(DirectionHint.relativeDegrees(0, -5, 0),-180); // behind
        near(DirectionHint.relativeDegrees(5, 0, -90),0); // east-facing
        assert Double.isNaN(DirectionHint.relativeDegrees(0,0,0));
        assert "ahead".equals(DirectionHint.label(0));
        assert "left".equals(DirectionHint.label(90));
        assert "right".equals(DirectionHint.label(-90));
        assert "behind".equals(DirectionHint.label(-180));
        assert "front-left".equals(DirectionHint.label(45));
        assert "front-right".equals(DirectionHint.label(-45));
        assert "right / 5 m".equals(DirectionHint.nearby(5,-90));
        assert "Nearby: 6 m".equals(DirectionHint.nearby(6,Double.NaN));
        System.out.println("DirectionHint tests passed");
    }
}
