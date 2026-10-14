package net.menear.betterdaycounters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DayCounterConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("better_day_counters.json");
    private static DayCounterConfig instance;

    public boolean enabled = true;
    public int anchor = 0; // 0 TL,1 TC,2 TR,3 CL,4 C,5 CR,6 BL,7 BC,8 BR
    public int margin = 4;
    public float scale = 1.0F;
    public int textColor = 0xFFFFFFFF;
    public boolean shadow = true;
    public boolean background = true;
    public int backgroundColor = 0x70000000;
    public int backgroundPadding = 3;
    public boolean romanNumerals = false;
    public boolean oneBasedDay = false;
    public String prefix = "Day ";
    public String font = "minecraft:default";

    public boolean rainbowText = false;
    public boolean rainbowBackground = false;
    public boolean glow = false;
    public boolean rainbowGlow = false;
    public int glowColor = 0x60FFFFFF;

    public static DayCounterConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static void save() {
        if (instance == null) {
            return;
        }
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(instance));
        } catch (IOException e) {
            BetterDayCountersMod.LOGGER.error("Failed to save config", e);
        }
    }

    private static DayCounterConfig load() {
        try {
            if (Files.exists(PATH)) {
                String json = Files.readString(PATH);
                DayCounterConfig cfg = GSON.fromJson(json, DayCounterConfig.class);
                if (cfg != null) {
                    return cfg;
                }
            }
        } catch (Exception e) {
            BetterDayCountersMod.LOGGER.warn("Failed to load config, using defaults", e);
        }
        DayCounterConfig cfg = new DayCounterConfig();
        instance = cfg;
        save();
        return cfg;
    }

    public static int hsvRgb(float hue) {
        float c = 1f, x = 1 - Math.abs((hue / 60f) % 2 - 1);
        float r = 0, g = 0, b = 0;
        int sector = (int) (hue / 60f) % 6;
        switch (sector) {
            case 0 -> { r = c; g = x; }
            case 1 -> { r = x; g = c; }
            case 2 -> { g = c; b = x; }
            case 3 -> { g = x; b = c; }
            case 4 -> { r = x; b = c; }
            default -> { r = c; b = x; }
        }
        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    public static int parseColor(String s, int fallback) {
        if (s == null) {
            return fallback;
        }
        s = s.trim();
        if (s.startsWith("#")) {
            s = s.substring(1);
        }
        if (s.startsWith("0x") || s.startsWith("0X")) {
            s = s.substring(2);
        }
        if (s.length() == 6) {
            s = "FF" + s;
        }
        if (s.length() != 8) {
            return fallback;
        }
        try {
            return (int) Long.parseLong(s, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static String formatColor(int c) {
        return String.format("%08X", c);
    }

    public static Identifier resolveFont() {
        try {
            return Identifier.parse(get().font);
        } catch (Exception e) {
            return Identifier.parse("minecraft:default");
        }
    }
}