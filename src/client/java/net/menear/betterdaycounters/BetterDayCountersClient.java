package net.menear.betterdaycounters;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BetterDayCountersClient implements ClientModInitializer {
    public static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        openConfigKey = new KeyMapping(
                "key.better_day_counters.open_config",
                InputConstants.KEY_K,
                KeyMapping.Category.MISC
        );

        try {
            Field field = Options.class.getDeclaredField("keyMappings");
            field.setAccessible(true);
            KeyMapping[] existing = (KeyMapping[]) field.get(Minecraft.getInstance().options);
            KeyMapping[] updated = Arrays.copyOf(existing, existing.length + 1);
            updated[updated.length - 1] = openConfigKey;
            field.set(Minecraft.getInstance().options, updated);
        } catch (Exception e) {
            BetterDayCountersMod.LOGGER.warn("Could not register keybinding in Controls menu", e);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.consumeClick() && client.gui.screen() == null) {
                client.setScreenAndShow(new DayCounterConfigScreen(null));
            }
        });

        DayCounterHud.register();
        DayCounterConfig.get();
        VanillaDayCounterBridge.apply(DayCounterConfig.get().enabled);
        BetterDayCountersMod.LOGGER.info("Better Day Counters client initialized.");
    }
}