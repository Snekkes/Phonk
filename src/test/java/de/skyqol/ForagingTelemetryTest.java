package de.skyqol;
/** Standalone test harness: javac .../ForagingTelemetry.java .../ForagingTelemetryTest.java && java -ea ... */
public final class ForagingTelemetryTest {
    public static void main(String[] args) {
        ForagingTelemetry state = new ForagingTelemetry();
        state.accept("+1,234 Oak Logs added to your sacks", false, 1000);
        assert state.logs() == 1234;
        assert "Logs seen: 1,234".equals(state.sessionDisplay(2000));
        assert state.sessionDisplay(61_000).contains("74,040/h");
        state.accept("+10 Diamonds added to your sacks", false, 1001);
        assert state.logs() == 1234;
        state.accept("Tree Progress: 78%", true, 2000);
        assert "Tree: 78%".equals(state.treeDisplay(2001));
        state.accept("Sweep: 35%", true, 2000);
        assert "Sweep: 35%".equals(state.sweepDisplay(2001));
        assert state.treeDisplay(20000) == null;
        state.reset();
        state.accept("+10 Oak Logs added to your sacks",false,40_000,false,true,false);
        assert state.logs() == 0 : "disabled logs must not track";
        state.accept("Tree Progress: 50%",true,40_000,false,false,true);
        assert state.treeDisplay(40_001) == null : "disabled tree display must not update";
        assert state.logs() == 0;
        System.out.println("Foraging telemetry parser checks passed.");
    }
}
