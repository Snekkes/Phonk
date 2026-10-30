package de.skyqol;
import java.util.List;
public final class HuntingTargetsTest {
    public static void main(String[] args) {
        assert "birries_highlighter".equals(HuntingTargets.classify("Birries", false));
        assert "hideonleaf_highlighter".equals(HuntingTargets.classify("§aHideonleaf", false));
        assert "shulker_finder".equals(HuntingTargets.classify("", true));
        assert HuntingTargets.classify("cow", false) == null;
        HuntingTargets t = new HuntingTargets();
        t.replace(List.of(new HuntingTargets.Sighting("birries_highlighter", "Birrie", 23.4),
            new HuntingTargets.Sighting("birries_highlighter", "Birrie", 7.6)), 1000);
        assert "Nearby: 8 m".equals(t.display("birries_highlighter", 1001));
        assert t.display("birries_highlighter", 5000) == null;
        t.replace(List.of(new HuntingTargets.Sighting("birries_highlighter", "Birrie", 3, 0)), 5100);
        assert "ahead / 3 m".equals(t.display("birries_highlighter", 5101));
        t.clear();
        assert t.display("birries_highlighter", 1001) == null;
        System.out.println("HuntingTargetsTest OK");
    }
}
