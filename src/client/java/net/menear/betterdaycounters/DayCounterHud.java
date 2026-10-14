package net.menear.betterdaycounters;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;

public final class DayCounterHud implements HudElement {
    private static final DayCounterHud INSTANCE = new DayCounterHud();

    private DayCounterHud() {
    }

    public static void register() {
        HudElementRegistry.addLast(BetterDayCountersMod.id("day_counter"), INSTANCE);
    }

    private static long getVanillaDay(Minecraft client) {
        try {
            java.util.Optional<? extends net.minecraft.core.Holder.Reference<Timeline>> optional =
                    client.level.registryAccess().get(Timelines.OVERWORLD_DAY);
            if (optional.isPresent()) {
                Timeline timeline = optional.get().value();
                return timeline.getPeriodCount(client.level.clockManager());
            }
        } catch (Exception ignored) {
        }
        try {
            return client.level.getOverworldClockTime() / 24000L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        DayCounterConfig cfg = DayCounterConfig.get();
        if (!cfg.enabled) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }

        long day = getVanillaDay(client);
        String number = cfg.romanNumerals ? RomanNumerals.toRoman(day) : Long.toString(day);
        String text = cfg.prefix + number;

        Font font = client.font;
        Style style = Style.EMPTY.withFont(new FontDescription.Resource(DayCounterConfig.resolveFont()));
        MutableComponent component = Component.literal(text).withStyle(style);

        int textWidth = font.width(component);
        int textHeight = font.lineHeight;

        float scale = Math.max(0.5F, Math.min(3.0F, cfg.scale));
        int screenWidth = extractor.guiWidth();
        int screenHeight = extractor.guiHeight();

        int unscaledWidth = (int) Math.ceil(screenWidth / scale);
        int unscaledHeight = (int) Math.ceil(screenHeight / scale);

        int x;
        int y;
        switch (cfg.anchor) {
            case 1 -> { x = (unscaledWidth - textWidth) / 2; y = cfg.margin; }
            case 2 -> { x = unscaledWidth - textWidth - cfg.margin; y = cfg.margin; }
            case 3 -> { x = cfg.margin; y = (unscaledHeight - textHeight) / 2; }
            case 4 -> { x = (unscaledWidth - textWidth) / 2; y = (unscaledHeight - textHeight) / 2; }
            case 5 -> { x = unscaledWidth - textWidth - cfg.margin; y = (unscaledHeight - textHeight) / 2; }
            case 6 -> { x = cfg.margin; y = unscaledHeight - textHeight - cfg.margin; }
            case 7 -> { x = (unscaledWidth - textWidth) / 2; y = unscaledHeight - textHeight - cfg.margin; }
            case 8 -> { x = unscaledWidth - textWidth - cfg.margin; y = unscaledHeight - textHeight - cfg.margin; }
            default -> { x = cfg.margin; y = cfg.margin; }
        }

        extractor.pose().pushMatrix();
        extractor.pose().scale(scale, scale);

        float hue = (System.currentTimeMillis() % 6000L) / 6000f * 360f;
        int textCol = cfg.rainbowText ? (0xFF000000 | DayCounterConfig.hsvRgb(hue)) : cfg.textColor;
        int bgCol = cfg.rainbowBackground ? ((cfg.backgroundColor & 0xFF000000) | DayCounterConfig.hsvRgb(hue)) : cfg.backgroundColor;
        int gCol = cfg.rainbowGlow ? ((cfg.glowColor & 0xFF000000) | DayCounterConfig.hsvRgb((hue + 180f) % 360f)) : cfg.glowColor;

        if (cfg.glow) {
            int gp = Math.max(0, cfg.backgroundPadding) + 4;
            extractor.fill(x - gp, y - gp, x + textWidth + gp, y + textHeight + gp, gCol);
        }

        if (cfg.background) {
            int pad = Math.max(0, cfg.backgroundPadding);
            extractor.fill(x - pad, y - pad, x + textWidth + pad, y + textHeight + pad, bgCol);
        }

        extractor.text(font, component, x, y, textCol, cfg.shadow);

        extractor.pose().popMatrix();
    }
}