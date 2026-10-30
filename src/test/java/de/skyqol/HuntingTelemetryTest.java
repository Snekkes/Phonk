package de.skyqol;

/** Run with java -ea; synthetic fixtures, not captured Hypixel traffic. */
public final class HuntingTelemetryTest {
    public static void main(String[] args) {
        HuntingTelemetry t = new HuntingTelemetry();
        t.accept("You received 12 Attribute Shards", false, 1000);
        assert t.shards() == 12;
        assert "Shards seen: 12".equals(t.shardSessionDisplay(2000));
        t.accept("+3 Ice Shards", false, 1001);
        assert t.shards() == 15;
        assert t.shardSessionDisplay(61_000).contains("900/h");
        t.accept("You received -3 shards", false, 1002);
        assert t.shards() == 15;
        t.accept("You received 10000000000 Shards", false, 1003);
        assert t.shards() == 15;
        t.accept("Fusion successful!", false, 1004);
        assert t.fusions() == 1;
        assert t.fusionDisplay(1005).contains("1");
        t.accept("Lasso: 57%", true, 2000);
        assert "Lasso: 57%".equals(t.lassoDisplay(2001));
        assert t.lassoDisplay(2000 + 16_000) == null;
        t.accept("Hunting Box: 13/24", true, 3000);
        assert "Hunting Box: 13/24".equals(t.huntingBoxDisplay(3001));
        assert t.huntingValueDisplay(0).contains("unknown");
        assert t.huntingValueDisplay(100).contains("1,500");
        HuntingTelemetry dedupe = new HuntingTelemetry();
        dedupe.accept("You received 12 Attribute Shards",false,10000);
        dedupe.accept("You received 12 Attribute Shards",false,10100);
        assert dedupe.shards()==12 : "Rapid duplicate should not count twice";
        dedupe.accept("You received 12 Attribute Shards",false,10260);
        assert dedupe.shards()==24;
        dedupe.accept("Fusion successful!",false,10500);
        dedupe.accept("Fusion successful!",false,10501);
        assert dedupe.fusions()==1;
        t.reset();
        t.accept("You received 12 Attribute Shards",false,40_000,false,true,true,true);
        assert t.shards() == 0 : "disabled shards must not count";
        t.accept("Fusion successful!",false,40_100,true,false,true,true);
        assert t.fusions() == 0 : "disabled fusion must not count";
        assert t.shards() == 0 && t.fusions() == 0 && t.lassoDisplay(3001) == null;
        System.out.println("HuntingTelemetryTest OK");
    }
}
