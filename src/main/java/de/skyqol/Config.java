package de.skyqol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persisted at config/skyqol.json; defaults are not automatically enabled. */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("skyqol.json");
    public Map<String, Boolean> enabled = new LinkedHashMap<>();
    public Map<String, HudPosition> hud = new LinkedHashMap<>();
    public boolean previewHud = false;
    /** User-defined approximate *gross* value for one observed shard; not market pricing. */
    public long estimatedShardValueCoins = 0;
    public static class HudPosition {
        public float x = 0.05f; // normalized GUI coordinates
        public float y = 0.12f;
        public float scale = 1.0f;
        /** Optional client-only appearance settings; 0.25..1.0. */
        public float opacity = 0.85f;
        public int accentRgb = 0x58C89B;
        public HudPosition() {}
        public HudPosition(float x, float y, float scale) { this.x=x; this.y=y; this.scale=scale; }
    }
    public void ensureDefaults() {
        if (enabled == null) enabled = new LinkedHashMap<>();
        if (hud == null) hud = new LinkedHashMap<>();
        estimatedShardValueCoins = Math.max(0, Math.min(100_000_000L, estimatedShardValueCoins));
        int idx = 0;
        for (var feature : Features.ALL) {
            enabled.putIfAbsent(feature.id(), false);
            if (feature.hasHud()) {
                hud.putIfAbsent(feature.id(), new HudPosition(0.04f+(idx%3)*0.30f, 0.10f+(idx/3)*0.15f, 1.0f));
                HudPosition pos = hud.get(feature.id());
                if (pos == null) { pos = new HudPosition(); hud.put(feature.id(), pos); }
                pos.scale = HudLayout.clampScale(pos.scale);
                pos.opacity = HudLayout.clampOpacity(pos.opacity);
                pos.accentRgb &= 0xFFFFFF;
                pos.x = Math.max(0, Math.min(1, Float.isFinite(pos.x) ? pos.x : 0));
                pos.y = Math.max(0, Math.min(1, Float.isFinite(pos.y) ? pos.y : 0));
                idx++;
            }
        }
    }
    public boolean enabled(String id) { return Boolean.TRUE.equals(enabled.get(id)); }
    public void toggle(String id) { enabled.put(id, !enabled(id)); save(); }
    public void save() {
        try {
            ensureDefaults();
            ConfigFileStore.save(FILE, GSON.toJson(this));
        } catch (Exception e) { SkyQolClient.LOGGER.error("Could not save SkyQoL config", e); }
    }
    public static Config load() {
        try {
            Config config = ConfigFileStore.load(FILE, json -> GSON.fromJson(json, Config.class));
            if (config == null) config = new Config();
            config.ensureDefaults();
            return config;
        } catch (Exception e) {
            SkyQolClient.LOGGER.error("Could not read SkyQoL config, falling back to defaults", e);
            Config config = new Config(); config.ensureDefaults(); return config;
        }
    }
}
