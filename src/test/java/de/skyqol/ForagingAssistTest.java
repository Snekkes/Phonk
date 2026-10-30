package de.skyqol;
public final class ForagingAssistTest {
    public static void main(String[] args) {
        ForagingAssist a = new ForagingAssist();
        a.accept("Tree Breaking Progress: 72%", true, 1000);
        assert "Break: 72%".equals(a.treeBreakDisplay(1001));
        a.accept("Tree Breaking Progress: 104%", true, 2000);
        assert "Break: 72%".equals(a.treeBreakDisplay(2001));
        assert a.treeBreakDisplay(20000) == null;
        a.accept("Foraging Beacon activated", false, 30000);
        assert a.beaconDisplay(30001).contains("solver pending");
        assert a.beaconDisplay(61001) == null;
        a.accept("Honeyhive", false, 70000);
        assert a.hasRecentHoneyhiveMention(70001);
        assert a.honeyhiveDisplay(70001) == null;
        a.startHoneyhive(80000);
        assert "Honeyhive: 5:00".equals(a.honeyhiveDisplay(80000));
        assert "Honeyhive: reminder due".equals(a.honeyhiveDisplay(380000));
        a.clearHoneyhive();
        assert a.honeyhiveDisplay(380001) == null;
        a.reset();
        a.accept("Foraging Beacon activated",false,400000,true,false,true);
        assert a.beaconDisplay(400001) == null : "disabled beacon must not update";
        assert a.beaconDisplay(380001) == null;
        System.out.println("Foraging assist tests passed");
    }
}
