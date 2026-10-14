package net.menear.betterdaycounters;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class VanillaDayCounterBridge {
    private static final Identifier DAY_COUNT = Identifier.parse("minecraft:day_count");
    private VanillaDayCounterBridge() {
    }

    public static void apply(boolean hideVanilla) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.debugEntries == null) {
            return;
        }
        if (hideVanilla) {
            client.debugEntries.setStatus(DAY_COUNT, net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.NEVER);
        }
        try {
            client.debugEntries.save();
        } catch (Exception ignored) {
        }
    }
}