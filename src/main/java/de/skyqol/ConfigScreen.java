package de.skyqol;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Modern Dark grouped config. Every switch persists immediately. */
public class ConfigScreen extends Screen {
    private String group = "Foraging";
    private int page;
    public ConfigScreen() { super(Component.literal("SkyQoL | Settings")); }
    private int left() { return Math.max(8,(width-368)/2); }
    private int top() { return Math.max(8,(height-250)/2); }
    @Override protected void init() { rebuild(); }
    private List<Features.Feature> current() {
        return Features.ALL.stream().filter(f->f.group().equals(group)).toList();
    }
    private void rebuild() {
        clearWidgets();
        int x=left(),y=top();
        addRenderableWidget(Button.builder(Component.literal("Foraging"),b->{group="Foraging";page=0;rebuild();}).bounds(x+12,y+36,104,20).build());
        addRenderableWidget(Button.builder(Component.literal("Hunting"),b->{group="Hunting";page=0;rebuild();}).bounds(x+124,y+36,104,20).build());
        addRenderableWidget(Button.builder(Component.literal("Edit HUD"),b->minecraft.gui.setScreen(new HudEditorScreen())).bounds(x+236,y+36,119,20).build());
        List<Features.Feature> list=current();
        int start=page*6;
        for(int i=start;i<Math.min(start+6,list.size());i++) {
            var f=list.get(i);
            int yy=y+70+(i-start)*23;
            boolean on=SkyQolClient.config.enabled(f.id());
            addRenderableWidget(Button.builder(Component.literal(on?"ON":"OFF"),b->{SkyQolClient.config.toggle(f.id());rebuild();})
                .bounds(x+289,yy,64,19).build());
        }
        addRenderableWidget(Button.builder(Component.literal("<"),b->{page=Math.max(0,page-1);rebuild();}).bounds(x+12,y+216,32,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->{page=Math.min((current().size()-1)/6,page+1);rebuild();}).bounds(x+50,y+216,32,20).build());
        addRenderableWidget(Button.builder(Component.literal("All ON"),b->{ setGroup(true);rebuild(); }).bounds(x+91,y+216,73,20).build());
        addRenderableWidget(Button.builder(Component.literal("All OFF"),b->{ setGroup(false);rebuild(); }).bounds(x+169,y+216,75,20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"),b->onClose()).bounds(x+256,y+216,98,20).build());
    }
    private void setGroup(boolean value) {
        for (var feature : current()) SkyQolClient.config.enabled.put(feature.id(),value);
        SkyQolClient.config.save();
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta) {
        int x=left(),y=top();
        // Panel first so widgets stay above the panel, on both OpenGL and Vulkan renderers.
        g.fill(x,y,x+368,y+246,0xF00E1721);
        g.fill(x,y,x+368,y+28,0xFF1F2F40);
        g.fill(x,y,x+4,y+28,0xFF52CD9B);
        g.fill(x+10,y+65,x+358,y+209,0x66314353);
        super.extractRenderState(g,mx,my,delta);
        g.text(font,"SkyQoL  /  Modern Dark",x+12,y+9,0xFFF2F8FF,true);
        g.text(font,"Auto saved",x+300,y+9,0xFF95B8AC,false);
        List<Features.Feature> list=current();
        for(int i=page*6;i<Math.min(page*6+6,list.size());i++) {
            int yy=y+70+(i-page*6)*23;
            var f=list.get(i);
            g.fill(x+17,yy+7,x+21,yy+12,SkyQolClient.config.enabled(f.id())?0xFF4ED4A0:0xFF758595);
            g.text(font,f.label(),x+27,yy+5,0xFFE1EAF4,false);
        }
        g.text(font,"Page "+(page+1)+"/"+((list.size()+5)/6)+" • 18 modules • passive / experimental",x+92,y+222,0xFF93A7BC,false);
    }
}
