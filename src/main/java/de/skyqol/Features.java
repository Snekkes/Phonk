package de.skyqol;

import java.util.List;

/** Stable IDs are persisted; changing them requires a config migration. */
public final class Features {
    public record Feature(String id, String label, String group, boolean hasHud) {}
    public static final List<Feature> ALL = List.of(
        new Feature("foraging_tracker", "Foraging Tracker", "Foraging", true),
        new Feature("tree_progress", "Tree Progress Display", "Foraging", true),
        new Feature("sweep_overlay", "Sweep Overlay", "Foraging", true),
        new Feature("beacon_solver", "Foraging Beacon Solver", "Foraging", true),
        new Feature("honeyhive_reminder", "Honeyhive Reminder", "Foraging", true),
        new Feature("tree_break_hud", "Tree Break Progress HUD", "Foraging", true),
        new Feature("invisibug_highlighter", "Invisibug Highlighter", "Foraging", true),
        new Feature("sea_lumies_highlighter", "Sea Lumies Highlighter", "Foraging", true),
        new Feature("critter_capsule", "Critter Capsule Helper", "Foraging", true),
        new Feature("hunting_profit", "Hunting Profit Tracker", "Hunting", true),
        new Feature("shard_tracker", "Shard Tracker Display", "Hunting", true),
        new Feature("fusion_display", "Fusion Display", "Hunting", true),
        new Feature("fusion_keybinds", "Fusion Keybinds", "Hunting", false),
        new Feature("lasso_hud", "Lasso HUD", "Hunting", true),
        new Feature("birries_highlighter", "Birries Highlighter", "Hunting", true),
        new Feature("hideonleaf_highlighter", "Hideonleaf Highlighter", "Hunting", true),
        new Feature("hunting_box", "Hunting Box Helper", "Hunting", true),
        new Feature("shulker_finder", "Shulker Finder", "Hunting", true)
    );
    private Features() {}
}
