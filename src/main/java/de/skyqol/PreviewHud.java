package de.skyqol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Live passive hints where available; explicit preview otherwise. */
public final class PreviewHud {
    private static final int CARD_W = HudDimensions.CARD_W, CARD_H = HudDimensions.CARD_H;
    private PreviewHud() {}
    public static void draw(GuiGraphicsExtractor graphics) {
        var mc=Minecraft.getInstance();
        var c=SkyQolClient.config;
        if(mc.player==null || mc.gui.screen()!=null) return;
        boolean hypixel = SkyQolClient.isOnHypixel(mc);
        if (!hypixel && !c.previewHud) return;
        long now=System.currentTimeMillis();
        for(var f:Features.ALL) {
            if(!f.hasHud() || !c.enabled(f.id())) continue;
            String detail = hypixel ? detail(f.id(),now) : null;
            if (detail == null && !c.previewHud) continue;
            if (detail == null) detail = "PREVIEW - no live data";
            var pos=c.hud.get(f.id());
            if(pos==null) continue;
            float scale=HudLayout.clampScale(pos.scale);
            int w=mc.getWindow().getGuiScaledWidth();
            int h=mc.getWindow().getGuiScaledHeight();
            int x=(int)(HudLayout.clampX(pos.x,scale,w,CARD_W)*w);
            int y=(int)(HudLayout.clampY(pos.y,scale,h,CARD_H)*h);
            graphics.pose().pushMatrix();
            graphics.pose().translate(x,y);
            graphics.pose().scale(scale,scale);
            int alpha=Math.round(255 * HudLayout.clampOpacity(pos.opacity));
            graphics.fill(0,0,CARD_W,CARD_H,(alpha << 24) | 0x001A2431);
            graphics.fill(0,0,3,CARD_H,0xFF000000 | (pos.accentRgb & 0xFFFFFF));
            graphics.text(mc.font,f.label(),8,5,0xFFFFFFFF,true);
            graphics.text(mc.font,clip(detail,30),8,18,0xFFB7C4D0,false);
            graphics.pose().popMatrix();
        }
    }
    private static String clip(String value,int limit) {
        return value.length()<=limit?value:value.substring(0,limit-3)+"...";
    }
    private static String detail(String id,long now) {
        return switch(id) {
            case "foraging_tracker" -> SkyQolClient.FORAGING.sessionDisplay(now);
            case "tree_progress" -> SkyQolClient.FORAGING.treeDisplay(now);
            case "sweep_overlay" -> SkyQolClient.FORAGING.sweepDisplay(now);
            case "tree_break_hud" -> SkyQolClient.ASSIST.treeBreakDisplay(now);
            case "beacon_solver" -> SkyQolClient.ASSIST.beaconDisplay(now);
            case "honeyhive_reminder" -> SkyQolClient.ASSIST.honeyhiveDisplay(now);
            case "invisibug_highlighter","sea_lumies_highlighter","critter_capsule" -> SkyQolClient.TARGETS.display(id,now);
            case "hunting_profit" -> SkyQolClient.HUNTING.huntingValueDisplay(SkyQolClient.config.estimatedShardValueCoins);
            case "shard_tracker" -> SkyQolClient.HUNTING.shardSessionDisplay(now);
            case "fusion_display" -> {
                String detected=SkyQolClient.HUNTING.fusionDisplay(now);
                String manual=SkyQolClient.FUSION_NOTES.display();
                yield detected == null ? manual : detected + (manual == null ? "" : " | " + manual);
            }
            case "lasso_hud" -> SkyQolClient.HUNTING.lassoDisplay(now);
            case "hunting_box" -> SkyQolClient.HUNTING.huntingBoxDisplay(now);
            case "birries_highlighter","hideonleaf_highlighter","shulker_finder" -> SkyQolClient.HUNTING_TARGETS.display(id,now);
            default -> null;
        };
    }
}
