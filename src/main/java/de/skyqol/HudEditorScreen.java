package de.skyqol;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Client-side editor for each HUD card. Mouse drag, scale, opacity, accent and group filter. */
public class HudEditorScreen extends Screen {
    private static final int CARD_W = HudDimensions.CARD_W, CARD_H = HudDimensions.CARD_H;
    private static final int HEADER = 82;
    private static final int[] ACCENTS = {0x58C89B, 0x89B8E8, 0xB894F4, 0xFFE083, 0xF8A0A0};
    private final List<Features.Feature> visible = new ArrayList<>();
    private Features.Feature dragging, selected;
    private String group = "Foraging";
    private double dragOffsetX, dragOffsetY;

    public HudEditorScreen() { super(Component.literal("SkyQoL HUD Editor")); }
    @Override protected void init() { rebuild(); }

    private Config.HudPosition position(Features.Feature feature) {
        return SkyQolClient.config.hud.get(feature.id());
    }
    private void refreshVisible() {
        visible.clear();
        for (var feature : Features.ALL)
            if (feature.hasHud() && feature.group().equals(group) && SkyQolClient.config.enabled(feature.id()))
                visible.add(feature);
        if (selected == null || !visible.contains(selected)) selected = visible.isEmpty() ? null : visible.get(0);
    }
    private void rebuild() {
        clearWidgets();
        refreshVisible();
        addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose()).bounds(8,8,52,20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset"), b -> resetGroup()).bounds(64,8,55,20).build());
        addRenderableWidget(Button.builder(Component.literal("Preview: " + (SkyQolClient.config.previewHud?"ON":"OFF")), b -> {
            SkyQolClient.config.previewHud = !SkyQolClient.config.previewHud;
            SkyQolClient.config.save(); rebuild();
        }).bounds(123,8,106,20).build());
        addRenderableWidget(Button.builder(Component.literal(group), b -> {
            group = group.equals("Foraging") ? "Hunting" : "Foraging";
            selected = null; dragging = null; rebuild();
        }).bounds(8,34,93,20).build());
        addRenderableWidget(Button.builder(Component.literal("Size -"), b -> resize(-.1f)).bounds(105,34,55,20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> resize(.1f)).bounds(164,34,24,20).build());
        addRenderableWidget(Button.builder(Component.literal("Fade -"), b -> fade(-.1f)).bounds(192,34,54,20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> fade(.1f)).bounds(250,34,24,20).build());
        addRenderableWidget(Button.builder(Component.literal("Color"), b -> color()).bounds(278,34,55,20).build());
    }
    private void resetGroup() {
        int i=0;
        for (var f:visible) {
            SkyQolClient.config.hud.put(f.id(), new Config.HudPosition(
                0.04f+(i%2)*.47f,0.22f+(i/2)*.19f,1f));
            i++;
        }
        SkyQolClient.config.save();
    }
    private void resize(float amount) {
        if (selected == null) return;
        var p=position(selected);
        p.scale=HudLayout.clampScale(Math.round((p.scale+amount)*10f)/10f);
        clamp(p);
        SkyQolClient.config.save();
    }
    private void fade(float amount) {
        if (selected == null) return;
        var p=position(selected);
        p.opacity=HudLayout.clampOpacity(Math.round((p.opacity+amount)*20f)/20f);
        SkyQolClient.config.save();
    }
    private void color() {
        if (selected == null) return;
        var p=position(selected);
        int next=0;
        for (int i=0;i<ACCENTS.length;i++) if (p.accentRgb==ACCENTS[i]) next=(i+1)%ACCENTS.length;
        p.accentRgb=ACCENTS[next];
        SkyQolClient.config.save();
    }
    private void clamp(Config.HudPosition p) {
        p.x=HudLayout.clampX(p.x,p.scale,width,CARD_W);
        p.y=HudLayout.clampEditorY(p.y,p.scale,height,CARD_H,HEADER);
    }
    private int px(Features.Feature f) { return Math.round(position(f).x*width); }
    private int py(Features.Feature f) { return Math.round(position(f).y*height); }

    @Override public boolean mouseClicked(MouseButtonEvent event,boolean doubleClick) {
        if (super.mouseClicked(event,doubleClick)) return true;
        double mx=event.x(),my=event.y();
        for (int i=visible.size()-1;i>=0;i--) {
            var f=visible.get(i);var p=position(f);
            int x=px(f),y=py(f);
            if (mx>=x && mx<x+CARD_W*p.scale && my>=y && my<y+CARD_H*p.scale) {
                selected=f;dragging=f;
                dragOffsetX=mx-x;dragOffsetY=my-y;
                return true;
            }
        }
        return false;
    }
    @Override public boolean mouseDragged(MouseButtonEvent event,double dx,double dy) {
        if (dragging!=null) {
            var p=position(dragging);
            p.x=(float)((event.x()-dragOffsetX)/Math.max(1,width));
            p.y=(float)((event.y()-dragOffsetY)/Math.max(1,height));
            clamp(p);
            return true;
        }
        return super.mouseDragged(event,dx,dy);
    }
    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging!=null) {dragging=null;SkyQolClient.config.save();return true;}
        return super.mouseReleased(event);
    }
    @Override public void onClose() {
        SkyQolClient.config.save();
        minecraft.gui.setScreen(new ConfigScreen());
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta) {
        // Draw background before controls: previously the header overlay covered widgets.
        g.fill(0,60,width,HEADER-4,0xE51A2836);
        String status=selected==null?"Select a card":String.format(Locale.ROOT,"%s | %.0f%% | alpha %.0f%%",
            selected.label(),position(selected).scale*100,position(selected).opacity*100);
        g.text(font,"Drag to move  |  "+status,8,64,0xFFDCF2F5,false);
        if (visible.isEmpty()) g.text(font,"Enable "+group+" HUDs in /skyqol first.",8,HEADER+10,0xFFAABACA,false);
        for (var f:visible) {
            var p=position(f);
            clamp(p); // handle resizing window without hiding the selected card off-screen
            int x=px(f),y=py(f);
            float scale=HudLayout.clampScale(p.scale);
            int alpha=Math.round(HudLayout.clampOpacity(p.opacity)*255);
            g.pose().pushMatrix();
            g.pose().translate(x,y);
            g.pose().scale(scale,scale);
            g.fill(0,0,CARD_W,CARD_H,(alpha<<24)|0x001F3040);
            g.fill(0,0,4,CARD_H,0xFF000000|(p.accentRgb&0xFFFFFF));
            g.text(font,f.label(),8,5,0xFFFFFFFF,true);
            g.text(font,"LAYOUT PREVIEW",8,19,selected==f?0xFFFFDE98:0xFFB0C2D2,false);
            g.pose().popMatrix();
        }
        // Widgets always render last, above the editor background and draggable cards.
        super.extractRenderState(g,mx,my,delta);
    }
}
