package de.skyqol;
import java.util.List;
public final class ForagingTargetsTest {
    public static void main(String[] args) {
        assert "invisibug_highlighter".equals(ForagingTargets.classify("§aInvisibug"));
        assert "sea_lumies_highlighter".equals(ForagingTargets.classify("Sea Lumies"));
        assert "critter_capsule".equals(ForagingTargets.classify("Critter Capsule"));
        assert ForagingTargets.classify("Unrelated entity") == null;
        ForagingTargets target = new ForagingTargets();
        target.replace(List.of(new ForagingTargets.Sighting("invisibug_highlighter", "Invisibug", 14.2), new ForagingTargets.Sighting("invisibug_highlighter", "Invisibug", 3.4)), 1000);
        assert "Nearby: 3 m".equals(target.display("invisibug_highlighter", 1200));
        assert target.display("invisibug_highlighter", 4500) == null;
        target.replace(List.of(new ForagingTargets.Sighting("invisibug_highlighter", "Invisibug", 5.2, -90)), 5000);
        assert "right / 5 m".equals(target.display("invisibug_highlighter", 5001));
        target.clear();
        assert target.display("invisibug_highlighter", 1200) == null;
        System.out.println("ForagingTargets tests passed");
    }
}
