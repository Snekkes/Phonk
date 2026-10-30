package de.skyqol;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Shulker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Client-only features. No automated clicking, movement, packets or gameplay macros. */
public class SkyQolClient implements ClientModInitializer {
    public static final Logger LOGGER=LoggerFactory.getLogger("SkyQoL");
    public static Config config;
    public static final ForagingTelemetry FORAGING=new ForagingTelemetry();
    public static final ForagingAssist ASSIST=new ForagingAssist();
    public static final ForagingTargets TARGETS=new ForagingTargets();
    public static final HuntingTelemetry HUNTING=new HuntingTelemetry();
    public static final HuntingTargets HUNTING_TARGETS=new HuntingTargets();
    public static final FusionNotes FUSION_NOTES = new FusionNotes();
    private KeyMapping openMenuKey, fusionMarkKey, fusionHudKey;
    private static final KeyMapping.Category HOTKEYS = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("skyqol","shortcuts"));
    private int scanTicks;
    @Override public void onInitializeClient() {
        config=Config.load();
        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.skyqol.open_menu", InputConstants.Type.KEYSYM, InputConstants.KEY_F8, HOTKEYS));
        fusionMarkKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.skyqol.fusion_note", InputConstants.Type.KEYSYM, InputConstants.KEY_F9, HOTKEYS));
        fusionHudKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.skyqol.toggle_fusion_hud", InputConstants.Type.KEYSYM, InputConstants.KEY_F10, HOTKEYS));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!isOnHypixel(Minecraft.getInstance())) return;
            long now=System.currentTimeMillis();
            String text=message.getString();
            // Avoid parsing every message when modules are switched off.
            if (config.enabled("foraging_tracker") || config.enabled("tree_progress") || config.enabled("sweep_overlay"))
                FORAGING.accept(text,overlay,now,
                    config.enabled("foraging_tracker"), config.enabled("tree_progress"), config.enabled("sweep_overlay"));
            if (config.enabled("beacon_solver") || config.enabled("honeyhive_reminder") || config.enabled("tree_break_hud"))
                ASSIST.accept(text,overlay,now,
                    config.enabled("tree_break_hud"), config.enabled("beacon_solver"), config.enabled("honeyhive_reminder"));
            if (config.enabled("shard_tracker") || config.enabled("hunting_profit") || config.enabled("fusion_display")
                    || config.enabled("lasso_hud") || config.enabled("hunting_box"))
                HUNTING.accept(text,overlay,now,
                    config.enabled("shard_tracker") || config.enabled("hunting_profit"),
                    config.enabled("fusion_display"), config.enabled("lasso_hud"), config.enabled("hunting_box"));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client) -> clearSession());
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.player != null && client.gui.screen() == null) client.gui.setScreen(new ConfigScreen());
            }
            while (fusionMarkKey.consumeClick()) {
                if (client.player != null && client.gui.screen() == null && isOnHypixel(client) && config.enabled("fusion_keybinds")) {
                    if (FUSION_NOTES.record(System.currentTimeMillis()))
                        client.player.sendSystemMessage(Component.literal("SkyQoL: manual fusion note recorded (not verified)."));
                }
            }
            while (fusionHudKey.consumeClick()) {
                if (client.player != null && client.gui.screen() == null && isOnHypixel(client) && config.enabled("fusion_keybinds")) {
                    config.toggle("fusion_display");
                    client.player.sendSystemMessage(Component.literal("SkyQoL: Fusion HUD " + (config.enabled("fusion_display") ? "on" : "off")));
                }
            }
            if (++scanTicks % 10 == 0) {
                if (isOnHypixel(client)) scanEntities(client);
                else { TARGETS.clear(); HUNTING_TARGETS.clear(); }
            }
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher,registryAccess) -> {
            dispatcher.register(ClientCommands.literal("skyqol").requires(source -> source.attended())
                .then(ClientCommands.literal("status").executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    long active = Features.ALL.stream().filter(f -> config.enabled(f.id())).count();
                    ctx.getSource().sendFeedback(Component.literal("SkyQoL v0.9.1 | " + active + "/18 enabled | Hypixel address: "
                        + (isOnHypixel(mc) ? "yes" : "no") + " | preview: " + (config.previewHud ? "on" : "off")));
                    ctx.getSource().sendFeedback(Component.literal("Observed this session: " + FORAGING.logs()
                        + " sack logs, " + HUNTING.shards() + " shards, " + HUNTING.fusions() + " fusion messages."));
                    return 1;
                }))
                .then(ClientCommands.literal("preview")
                    .then(ClientCommands.literal("on").executes(ctx -> { config.previewHud=true;config.save();return 1; }))
                    .then(ClientCommands.literal("off").executes(ctx -> { config.previewHud=false;config.save();return 1; })))
                .then(ClientCommands.literal("honeyhive")
                    .then(ClientCommands.literal("start").executes(ctx -> {ASSIST.startHoneyhive(System.currentTimeMillis());return 1;}))
                    .then(ClientCommands.literal("clear").executes(ctx -> {ASSIST.clearHoneyhive();return 1;})))
                .then(ClientCommands.literal("hud").executes(ctx -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(new HudEditorScreen()));
                    return 1;
                }))
                .then(ClientCommands.literal("fusion")
                    .then(ClientCommands.literal("note").executes(ctx -> { FUSION_NOTES.record(System.currentTimeMillis()); return 1; }))
                    .then(ClientCommands.literal("clear").executes(ctx -> { FUSION_NOTES.reset(); return 1; })))
                .then(ClientCommands.literal("tracking")
                    .then(ClientCommands.literal("reset").executes(ctx -> {clearSession();return 1;})))
                .then(ClientCommands.literal("shardvalue")
                    .then(ClientCommands.argument("coins",IntegerArgumentType.integer(0,100_000_000))
                        .executes(ctx -> {
                            config.estimatedShardValueCoins=IntegerArgumentType.getInteger(ctx,"coins");
                            config.save();
                            return 1;
                        })))
                .executes(context -> {
                    Minecraft mc=Minecraft.getInstance();
                    mc.execute(() -> mc.gui.setScreen(new ConfigScreen()));
                    return 1;
                }));
        });
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("skyqol","hud"), (graphics,ticks) -> PreviewHud.draw(graphics));
        LOGGER.info("SkyQoL started: {} switches",Features.ALL.size());
    }
    private static void clearSession() {
        FORAGING.reset();ASSIST.reset();TARGETS.clear();
        HUNTING.reset();HUNTING_TARGETS.clear();FUSION_NOTES.reset();
    }
    /** Connection check only; never logs or transmits addresses. */
    static boolean isOnHypixel(Minecraft client) {
        var server = client.getCurrentServer();
        return server != null && HypixelServer.matches(server.ip);
    }

    private static void scanEntities(Minecraft client) {
        if (client.level==null || client.player==null) { TARGETS.clear();HUNTING_TARGETS.clear();return; }
        boolean foraging=config.enabled("invisibug_highlighter") || config.enabled("sea_lumies_highlighter") || config.enabled("critter_capsule");
        boolean hunting=config.enabled("birries_highlighter") || config.enabled("hideonleaf_highlighter") || config.enabled("shulker_finder");
        if (!foraging && !hunting) {TARGETS.clear();HUNTING_TARGETS.clear();return;}
        List<ForagingTargets.Sighting> fTargets=new ArrayList<>();
        List<HuntingTargets.Sighting> hTargets=new ArrayList<>();
        for (var entity:client.level.entitiesForRendering()) {
            if (fTargets.size()+hTargets.size()>=64) break;
            double d=Math.sqrt(entity.distanceToSqr(client.player));
            if (d>96) continue;
            String name=entity.hasCustomName()?entity.getCustomName().getString():null;
            // Player yaw relative to target; only loaded client-renderable entities are scanned.
            double relative = DirectionHint.relativeDegrees(entity.getX()-client.player.getX(),
                entity.getZ()-client.player.getZ(), client.player.getYRot());
            if (foraging && name!=null) {
                String type=ForagingTargets.classify(name);
                if (type!=null && config.enabled(type)) fTargets.add(new ForagingTargets.Sighting(type,name,d,relative));
            }
            if (hunting) {
                String type=HuntingTargets.classify(name,entity instanceof Shulker);
                if (type!=null && config.enabled(type)) hTargets.add(new HuntingTargets.Sighting(type,name==null?"Shulker":name,d,relative));
            }
        }
        long now=System.currentTimeMillis();
        if (foraging) TARGETS.replace(fTargets,now); else TARGETS.clear();
        if (hunting) HUNTING_TARGETS.replace(hTargets,now); else HUNTING_TARGETS.clear();
    }
}
